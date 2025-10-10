import glob
import numpy as np
import matplotlib.pyplot as mplt
import pandas as pd
from collections import deque


def ler_ultimas_linhas(caminho_arquivo):
    with open(caminho_arquivo, 'r') as arquivo:
        linhas = arquivo.readlines()  # todas as linhas do arquivo em uma lista
        return linhas[-16:-13]


def extrair_valores(linha):
    return float(linha.split(":")[1].strip())


# Percorrer os arquivos e criar listas para dataframe:
arquivos = glob.glob("*.txt")
trocas = []
instrucoes = []
quantuns = []
linhas = []

for caminho in arquivos:
    linhas = ler_ultimas_linhas(caminho)

    trocas.append(extrair_valores(linhas[0]))
    instrucoes.append(extrair_valores(linhas[1]))
    quantuns.append(extrair_valores(linhas[2]))


# Criação do DataFrame:
df = pd.DataFrame({
    'Trocas de Contexto': trocas,
    'Instruções': instrucoes,
    'Quantum': quantuns
})

print(df)


# Gráfico:

mplt.figure(figsize=(10, 6))
mplt.plot(df['Quantum'], df['Trocas de Contexto'], marker='o', label="Trocas")
mplt.plot(df['Quantum'], df['Instruções'], marker='s', label="Instruções")

mplt.title("Comparação de Instruções e Trocas de Contexto por Quantum")
mplt.xlabel('Quantum')
mplt.ylabel("Quantidade")
mplt.legend()
mplt.grid(True)

mplt.savefig("grafico_comparativo_quantum.png")
mplt.show()
