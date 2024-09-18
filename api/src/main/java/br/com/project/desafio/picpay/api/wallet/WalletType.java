package br.com.project.desafio.picpay.api.wallet;

public enum WalletType {
    COMUM(1), STORE(2);

    private final   int value;

    private WalletType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
