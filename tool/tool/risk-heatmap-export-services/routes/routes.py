from flask import Blueprint, request, jsonify
from services.ai_service import generate_response

main = Blueprint('main', __name__)

@main.route('/')
def home():
    return jsonify({"message": "AI Service Running 🚀"})

@main.route('/describe', methods=['POST'])
def describe():
    data = request.get_json()

    if not data or "input" not in data:
        return jsonify({"error": "Invalid input"}), 400

    user_input = data["input"]

    result = generate_response(user_input)

    return jsonify(result)