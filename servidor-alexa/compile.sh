#!/bin/bash

#Readme: Para definir este arquivo como executável rode o comando "chmod +x compile_lx.sh" antes de executar o arquivo
#        Para executar o arquivo digite> ./compile_lx.sh

echo Definir o caminho do diretório atual
current_dir="$(pwd)"

echo Compilar todos os arquivos .java
javac -encoding UTF-8 -cp "$current_dir/dependencies.jar" -d "$current_dir/out" "$current_dir/src/servalexa/main.java" "$current_dir/src/servalexa/tools"/*.java -Xlint

echo Verificar se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro durante a compilação."
    exit 1
else
    echo "Compilação bem-sucedida."
fi