import os
from datetime import datetime
from groq import Groq
from dotenv import load_dotenv

load_dotenv()

def load_prompt():
    prompt_path = os.path.join(os.path.dirname(__file__), "..", "prompts", "risk_prompt.txt")
    if os.path.exists(prompt_path):
        with open(prompt_path, "r") as f:
            return f.read()
    return "Analyze this risk: {input}"

def generate_response(user_input):
    try:
        prompt_template = load_prompt()
        final_prompt = prompt_template.replace("{input}", user_input)

        client = Groq(api_key=os.getenv("GROQ_API_KEY"))

        response = client.chat.completions.create(
            messages=[
                {
                    "role": "user",
                    "content": final_prompt
                }
            ],
            model="llama-3.1-8b-instant"
        )

        if response and response.choices:
            output = response.choices[0].message.content
        else:
            output = "No AI response generated"

        return {
            "result": output,
            "generated_at": datetime.now().isoformat()
        }

    except Exception as e:
        return {
            "result": "AI service temporarily unavailable",
            "error": str(e),
            "generated_at": datetime.now().isoformat()
        }