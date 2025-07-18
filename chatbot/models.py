from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from database import Base

class Eleccion(Base):
    __tablename__ = "elecciones"
    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String)

class Candidato(Base):
    __tablename__ = "candidatos"
    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String)
    eleccion_id = Column(Integer, ForeignKey("elecciones.id"))
    eleccion = relationship("Eleccion")
