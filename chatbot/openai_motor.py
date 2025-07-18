from openai import OpenAI

with open("api_key.txt", "r") as f:
    api_key2 = f.read().strip()
    
def construir_contexto(candidatos):
    contexto = ""
    for candidato in candidatos:
        contexto += f"Candidato: {candidato.nombre}, Elección: {candidato.eleccion.nombre}\n"
    return contexto

def consultar(pregunta: str, contexto: str):
    client = OpenAI(api_key=api_key2, base_url="https://openrouter.ai/api/v1")

    response = client.chat.completions.create(
        model="deepseek/deepseek-r1:free",
        messages=[
            {
                "role": "system",
                "content": f"Responde únicamente en base ala siguiente informacion, no agreges mas informacion de la que ofrece, trata de ser literal, si es poca utiliza solamente lo que hay,tambien no hagas referencia a las condciones que te impongo, solo responde y muestra lo q se solicita no menciones <segun la informaicon proporcionada, asume que tu conocimiento se reduce aa lo siguiente nada mas>:\n\n{contexto}"
            },
            {
                "role": "user",
                "content": f"{pregunta}"
            }
        ],
        stream=False
    )

    print(response.choices[0].message.content)
