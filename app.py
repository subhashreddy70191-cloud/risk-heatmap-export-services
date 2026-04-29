from flask import Flask, request, jsonify
from services.ai_service import generate_response

app = Flask(__name__)

@app.route("/describe", methods=["POST"])
def describe():
    data = request.json
    user_input = data.get("input")

    result = generate_response(user_input)

    return jsonify(result)


@app.route("/recommend", methods=["POST"])
def recommend():
    data = request.json
    user_input = data.get("input")

    recommendations = [
        {
            "action_type": "Mitigation",
            "description": "Assign extra team members to complete pending work",
            "priority": "High"
        },
        {
            "action_type": "Monitoring",
            "description": "Track daily progress and review blockers",
            "priority": "Medium"
        },
        {
            "action_type": "Communication",
            "description": "Inform stakeholders about possible delay",
            "priority": "Low"
        }
    ]

    return jsonify(recommendations)



    app.run(debug=True)
@app.route("/generate-report", methods=["POST"])
def generate_report():
    data = request.json
    user_input = data.get("input")

    report = {
        "title": "Project Risk Assessment Report",
        "summary": "The project has approaching deadlines with incomplete work and possible delivery risks.",
        "overview": f"Analysis based on input: {user_input}",
        "key_items": [
            "Deadline is very close",
            "Pending work remains incomplete",
            "Team coordination required"
        ],
        "recommendations": [
            "Assign additional team support",
            "Track progress daily",
            "Inform stakeholders early"
        ]
    }

    return jsonify(report)
if __name__ == "__main__":
 app.run(debug=True)