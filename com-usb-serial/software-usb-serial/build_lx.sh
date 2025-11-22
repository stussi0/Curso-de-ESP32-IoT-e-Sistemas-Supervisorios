#!/bin/bash

#Readme: Para definir este arquivo como executável rode o comando "chmod +x build_lx.sh" antes de executar o arquivo
#        Para executar o arquivo digite> ./build_lx.sh

echo Definir o caminho do diretório atual
current_dir="$(dirname "$0")"

echo "Class-Path: file:$PWD/out/ file:$PWD/libs/jSerialComm-2.9.2.jar" > manifest.txt
jar cvfm dependencies.jar manifest.txt
echo Removendo o arquivo manifest temporario...
rm manifest.txt

echo Compilar todos os arquivos .java
javac -encoding UTF-8 -cp "$PWD/dependencies.jar" -d "$PWD/out" "$PWD/src/comunicador/DataAquisition.java"

echo Verificar se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro durante a compilação."
    exit 1
else
    echo "Compilação bem-sucedida."
fi

echo Criar o arquivo .jar com classe executável
cd out
echo "Main-Class: comunicador/DataAquisition" > manifest.txt
jar cvfm main.jar manifest.txt comunicador/*.class

echo Verificar se a criação do .jar foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro durante a criação do arquivo JAR."
    exit 1
else
    echo "Arquivo JAR criado com sucesso."
fi

echo Remover o arquivo manifest temporário
rm manifest.txt

echo Gerando javadocs...
cd ..
javadoc -encoding UTF-8 -cp "$current_dir/dependencies.jar" -d docs -sourcepath src -subpackages comunicador

echo Testando compilacao:
java -cp dependencies.jar comunicador.DataAquisition

