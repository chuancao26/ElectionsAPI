from sqlalchemy.orm import Session
from models import Candidato, Eleccion

def obtener_candidatos(db: Session):
    return db.query(Candidato).all()

def obtener_eleccion(db: Session, eleccion_id: int):
    return db.query(Eleccion).filter(Eleccion.id == eleccion_id).first()
