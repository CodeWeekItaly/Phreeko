import json
import logging

from util import calculate_token, password_parsing, is_mail, validate_token, decrypt_token, parse_company_search
from string import printable  # used for whitelist
from flask import Flask, request, Response, abort, jsonify  # client -> this
from requests import post  # this -> server

# database
database_url = "http://196fdbfedaeb.ngrok.io"  # TODO: change this
counter = 4528687

# whitelist
whitelist = set(printable)

# flask
app = Flask(__name__)
logging.basicConfig(level=logging.DEBUG)
app.config["DEBUG"] = True


@app.errorhandler(Exception)
def http_error_handler(error):
    logging.error(error)
    return jsonify(status=500, error="unknown error"), 500  # TODO: better error logging


@app.route("/search", methods=["POST"])
def search():
    data = request.get_json(force=True)
    token = request.form['token']  # string
    delivery = data["delivery"]  # 1 -> at home, 0 -> no  boolean
    coords = data["coordinates"]  # [latitude, longitude]

    if not validate_token(token):
        abort(401)  # Unauthorized

    payload = {  # conversion kilometers to delta coords
                 # for now .25 is like 30 km
        "minLat": coords[0] - .25,
        "maxLat": coords[0] + .25,
        "minLng": coords[1] - .25,
        "maxLng": coords[1] + .25
    }

    response = post(url=database_url + "/db/company/from_range", json=payload)  # TODO

    data = parse_company_search(response.json(), delivery)

    # status check and return
    if response.status_code == 200:
        return json.dumps(data), 200
    elif response.status_code == 404:
        return jsonify(status=response.status_code, error="not found"), response.status_code
    else:
        return jsonify(status=response.status_code, error="unknown error"), response.status_code


@app.route("/resolve", methods=["POST"])
def resolve():
    """
    TODO: implement auth
    Data example:
    Send username or id, this will return both user and associate id
    {
        "username": "example"
    }

    :return: status code == 200
    {
        "user_id": 123456,
        "username": "example"
    }
    :exception: status code != 200
    {
        "status": 550,
        "error": "error explained
    }
    """
    data = request.get_json(force=True)

    if "username" in data.keys():
        payload = {
            "user": data["username"]
        }
    else:
        payload = {
            "id": data["user_id"]
        }

    response = post(url=database_url + "/db/user/convert_id_name", json=payload)

    # status check and return
    if response.status_code == 200:
        data = response.json()
        return jsonify(username=data["user"], user_id=data["id"]), 200
    elif response.status_code == 404:
        return jsonify(status=response.status_code, error="not found"), response.status_code
    else:
        return jsonify(status=response.status_code, error="unknown error"), response.status_code


@app.route("/modify", methods=["POST"])
def modify():
    """
    Data example:
    Send mail, password, phone, and token to update user data
    {
        "password": "amungus",
        "token": "2838"
    }

    :return: status code == 200, returns new token
    {
        "token":"38738"
    }
    :exception: status code != 200
    {
        "status": 550,
        "error": "error explained
    }
    """
    data = request.get_json(force=True)
    print(data)
    token = data["token"]
    user_id, mail, username, password = decrypt_token(token)

    payload = data
    if password in payload.keys():
        payload["password"] = password_parsing(payload["password"])
    del payload["token"]

    response = post(url=database_url + "/db/user/change_user", json=payload)

    # status check and return
    if response.status_code == 201:
        return jsonify(token=calculate_token(user_id, mail, username, password)), 200
    else:
        return jsonify(status=response.status_code, error="cannot register this account"), response.status_code


@app.route("/login", methods=["POST"])
def login():
    """
    Data example:
    Send mail or username, and password to get a token
    {
        "username": "jojo@example.com",
        "password": "sus"
    }

    :return: status code == 200, returns new token
    {
        "token":"38798738"
    }
    :exception: status code != 200
    {
        "status": 550,
        "error": "error explained
    }
    """
    data = request.get_json(force=True)
    username = data["username"]
    password = password_parsing(data["password"])  # password hashing sha256

    # sanity check
    if any(c not in whitelist for c in username):
        logging.warning(f"{request.remote_addr} used a username with not whitelisted characters")
        return jsonify(status=406, error="username uses invalid characters"), 406  # Not Acceptable
    elif len(username) > 32:
        logging.warning(f"{request.remote_addr} exceed max username length 32")
        return jsonify(status=406, error="username length exceeded"), 406  # Not Acceptable

    # mail or username, nobody knows
    if is_mail(username):
        payload = {"mail": username,
                   "password": password}
    else:
        payload = {"username": username,
                   "password": password}

    # send request to database
    response = post(url=f"{database_url}/db/user/login", json=payload)

    # status check and return
    if response.status_code == 200:
        userid = response.json()["id"]
        mail = response.json()["email"]
        username = response.json()["email"]
        return jsonify(token=calculate_token(userid, mail, username, password)), 200
    elif response.status_code == 404:
        return jsonify(status=response.status_code, error="account not found"), response.status_code
    else:
        return jsonify(status=response.status_code, error="cannot login this account"), response.status_code


@app.route("/register", methods=["POST"])
def register():
    """
    Data example:
    Send mail, username, password and phone to register a new account
    {
        "mail": "jojo@example.com",
        "username": "lmao",
        "phone": "69420666",
        "password": "sus"
    }

    :return: status code == 200, returns new token
    {
        "token":"38798738"
    }
    :exception: status code != 200
    {
        "status": 550,
        "error": "error explained
    }
    """

    # very horrible way to store counter, but for now it will works
    global counter
    counter += 1

    # get data from request
    data = request.get_json(force=True)
    username = data["username"]
    mail = data["mail"]
    password = password_parsing(data["password"])
    phone = data["phone"]

    # sanity checks
    if any(c not in whitelist for c in username):
        logging.warning(f"{request.remote_addr} used a username with not whitelisted characters")
        return jsonify(status=406, error="username uses invalid characters"), 406  # Not Acceptable
    elif len(username) > 32:
        logging.warning(f"{request.remote_addr} exceed max username length 32")
        return jsonify(status=406, error="username length exceeded"), 406  # Not Acceptable
    elif len(username) > 256:
        logging.warning(f"{request.remote_addr} exceed max mail length 256")
        return jsonify(status=406, error="mail length exceeded"), 406  # Not Acceptable

    # build payload
    payload = {"username": username,
               "email": mail,
               "password": password,
               "phone": phone,
               "id": counter}

    # send request to database
    response = post(url=f"{database_url}/db/user/register", json=payload)
    # status check and return
    if response.status_code == 201:
        return jsonify(token=calculate_token(str(counter), mail, username, password)), response.status_code
    elif response.status_code == 409:
        return jsonify(status=response.status_code, error="already exist"), response.status_code
    else:
        return jsonify(status=response.status_code, error="cannot register this account"), response.status_code
