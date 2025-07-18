from fastapi import APIRouter, Depends
from pydantic import BaseModel
from sqlalchemy.orm import Session
from database import SessionLocal
from crud import obtener_candidatos
from openai_motor import consultar

router = APIRouter()   

class ChatRequest(BaseModel):
    pregunta: str

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/chat")
def chat(request: ChatRequest, db: Session = Depends(get_db)):
    context = obtener_candidatos(db)  
    respuesta = consultar(request.pregunta, context)
    return {"respuesta": respuesta}
