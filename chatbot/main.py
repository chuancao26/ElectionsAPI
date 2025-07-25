from fastapi import FastAPI
from chat import router

app = FastAPI(title="Chatbot Elecciones API")


app.include_router(router)
#port 8020
