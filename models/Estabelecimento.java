package com.vitalis.saude.models;

public class Estabelecimento {
    public static final int TIPO_HOSPITAL = 0;
    public static final int TIPO_POSTO = 1;
    public static final int TIPO_FARMACIA = 2;

    private String nome;
    private String endereco;
    private int tipo;

    public Estabelecimento(String nome, String endereco, int tipo) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
    }

    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public int getTipo() { return tipo; }

    public String getIcon() {
        switch (tipo) {
            case TIPO_HOSPITAL: return "🏥";
            case TIPO_POSTO: return "🏥";
            case TIPO_FARMACIA: return "💊";
            default: return "📍";
        }
    }
}