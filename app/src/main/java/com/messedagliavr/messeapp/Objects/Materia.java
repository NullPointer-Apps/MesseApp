package com.messedagliavr.messeapp.Objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class Materia
{

  private String nome = "null";
  private String recupero = "";
  private HashMap<Integer,Voto> voti;

  public Materia(String n)
  {
    nome = n.toUpperCase().trim();
    voti = new HashMap<>();
  }
  public Materia()
    {
        voti = new HashMap<>();
    }

  public double mediaVoti(String tipo, int q){
      HashMap<Integer,Voto> vv = getVoti();
      int n = vv.size();
      double somma=0;
      for (Voto v : vv.values()){
          String voto = v.getVoto();
          if (((int)voto.charAt(0)!=71)&&(v.getTipo().equals(tipo)||tipo.equals("tutti"))&&(v.getQuadrimestre()==q||q==3)) {
              somma += (int) voto.charAt(0) - 48;
              if (voto.length()>1) {
                  if (voto.charAt(1) == '-') {
                      somma -= 0.25;
                  } else if (voto.charAt(1) == '+') {
                      somma += 0.25;
                  } else if (voto.charAt(1) == '½') {
                      somma += 0.5;
                  }
              }
          } else n--;
      }
      if (n!=0) {
          BigDecimal bd = new BigDecimal(somma / n);
          bd = bd.setScale(2, RoundingMode.HALF_UP);
          return bd.doubleValue();
      } else return 0;
  }

  public boolean haVoti(String tipo, int q){
      HashMap<Integer,Voto> vv = getVoti();
      for (Voto v : vv.values()){
          if ((v.getQuadrimestre() == q||q==3) && (v.getTipo().equals(tipo)||tipo.equals("tutti"))) return true;
      }
      return false;
  }
  public void addVoto(int k, Voto v)
  {
    this.voti.put(k,v);
  }
  public String getNome()
  {
    return this.nome;
  }

  public String getRecupero()
  {
    return this.recupero;
  }

  public HashMap<Integer,Voto> getVoti()
  {
    return this.voti;
  }

  public void setRecupero(String paramString)
  {
    this.recupero = paramString;
  }

}
