from sqlalchemy.orm import Session

def obtener_candidatos(db: Session) -> str:
    result = db.execute("SELECT * FROM voting_event")
    candidatos = result.fetchall()
    if not candidatos:
        return "No hay candidatos registrados."
    
    return "\n".join(f"- {nombre}" for nombre in candidatos)
