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