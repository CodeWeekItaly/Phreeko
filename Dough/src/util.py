import re

from os import urandom  # used for token generation
from binascii import hexlify  # used for token generation
from hashlib import sha256

from src.algorithm.algorithm import preference_sorting

mail_validation = r"^(\w|\.|\_|\-)+[@](\w|\_|\-|\.)+[.]\w{2,3}$"


def validate_token(token: str):
    # TODO: create an algorithm of token validation
    return True


def decrypt_token(token: str):
    """
    Get username and password from auth token

    :param token: token
    :return: (id, mail, username, password)
    """
    return token[18:len(token) - 18].split("//")[1].split(":")


def calculate_token(user_id: str, mail: str, username: str, password: str) -> str:
    # TODO: create an secure algorithm of token generation
    return f"{hexlify(urandom(16)).decode()}//{user_id}:{mail}:{username}:{password}//{hexlify(urandom(16)).decode()}"


def password_parsing(password: str) -> str:
    return sha256(password.encode("ascii")).hexdigest()


def is_mail(mail: str) -> bool:
    if re.search(mail_validation, mail) is not None:
        return True
    else:
        return False


def parse_company_search(data: dict, delivery: bool = False) -> dict:
    # TODO: implement sorting algorithm
    if delivery:
        result = []
        # filter for home delivery
        for company in data:
            if company["delivery"]:
                result.append(company)
        # sort data using THE ALGORITHM
        result = preference_sorting(result)
        return result
    else:
        # sort data using THE ALGORITHM
        result = preference_sorting(data)
        return result
