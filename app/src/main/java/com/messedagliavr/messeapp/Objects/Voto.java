package com.messedagliavr.messeapp.Objects;

public class Voto
{
  private String data;
  private int quadrimestre;
  private String tipo = "";
  private String voto;

  public Voto()
  {
    this.quadrimestre = 1;
  }

  public String getData()
  {
    return this.data;
  }

  public int getQuadrimestre()
  {
    return this.quadrimestre;
  }

  public String getTipo()
  {
    return this.tipo;
  }

  public String getVoto()
  {
    return this.voto;
  }

  public void setData(String paramString)
  {
    this.data = paramString.trim();
  }

  public void setQuadrimestre(int paramInt)
  {
    this.quadrimestre = paramInt;
  }

  public void setTipo(String paramString)
  {
    this.tipo = paramString.trim();
  }

  public void setVoto(String paramString)
  {
    this.voto = paramString.trim();
  }
}
