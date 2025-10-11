import os
import glob
import numpy as np
import matplotlib.pyplot as mplt
import pandas as pd


def extrair_metricas(caminho_arquivo):
    """
    Lê um arquivo de log e extrai:
      - média de trocas de contexto
      - média de instruções por quantum
      - média de turnaround
      - quantum utilizado
    """
    troca = instr = turnaround = quantum = None
    with open(caminho_arquivo, 'r', encoding='utf-8') as arquivo:
        for linha in arquivo:
            linha_limpa = linha.strip().upper()
            if "MEDIA DE TROCAS" in linha_limpa:
                troca = float(linha_limpa.split(":")[1])
            elif "MEDIA DE INSTRUCOES" in linha_limpa:
                instr = float(linha_limpa.split(":")[1])
            elif "MEDIA DE TURNAROUND" in linha_limpa:
                turnaround = float(linha_limpa.split(":")[1])
            elif "QUANTUM:" in linha_limpa:
                quantum = float(linha_limpa.split(":")[1])
    return troca, instr, turnaround, quantum


# 🔹 Caminho da pasta onde o script está
base = os.path.dirname(os.path.abspath(__file__))

# 🔹 Busca todos os .txt dentro dessa pasta
arquivos = glob.glob(os.path.join(base, "*.txt"))
print(f"Arquivos encontrados: {arquivos}")

trocas, instrucoes, turnarounds, quantuns = [], [], [], []

for caminho in arquivos:
    t, i, ta, q = extrair_metricas(caminho)
    print(f"{os.path.basename(caminho)} -> Trocas={t}, Instruções={i}, Turnaround={ta}, Quantum={q}")
    if t is not None and i is not None and ta is not None and q is not None:
        trocas.append(t)
        instrucoes.append(i)
        turnarounds.append(ta)
        quantuns.append(q)

# Cria DataFrame
df = pd.DataFrame({
    "Quantum": quantuns,
    "Trocas de Contexto": trocas,
    "Instruções": instrucoes,
    "Turnaround": turnarounds
}).sort_values(by="Quantum")

print("\nDataFrame final:")
print(df)
print(f"Shape do DataFrame: {df.shape}")

# Gera gráfico somente se tiver dados
if not df.empty:
    mplt.figure(figsize=(10, 6))
    mplt.plot(df["Quantum"], df["Trocas de Contexto"], marker="o", label="Trocas de Contexto")
    mplt.plot(df["Quantum"], df["Instruções"], marker="s", label="Instruções por Quantum")
    mplt.plot(df["Quantum"], df["Turnaround"], marker="^", label="Turnaround Médio", linestyle="--")

    mplt.title("Comparação de Instruções, Trocas e Turnaround por Quantum")
    mplt.xlabel("Quantum")
    mplt.ylabel("Média")
    mplt.legend()
    mplt.grid(True)
    mplt.savefig(os.path.join(base, "grafico_comparativo_quantum.png"))
    mplt.show()
else:
    print("\n⚠️ Nenhum dado encontrado nos arquivos .txt!")
