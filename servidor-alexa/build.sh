#!/bin/bash

#Readme: Para definir este arquivo como executável rode o comando "chmod +x build_lx.sh" antes de executar o arquivo
#        Para executar o arquivo digite> ./build_lx.sh

echo Definir o caminho do diretório atual
current_dir="$(pwd)"

echo "$current_dir"

echo "Class-Path: file:$current_dir/ file:$current_dir/libs/ file:$current_dir/libs/jSerialComm-2.9.2.jar" > manifest.txt
jar cvfm dependencies.jar manifest.txt
echo Removendo o arquivo manifest temporario...
rm manifest.txt

echo Compilar todos os arquivos .java
javac -encoding UTF-8 -cp "$current_dir/dependencies.jar" -d "$current_dir/out" "$current_dir/src/servalexa/main.java" "$current_dir/src/servalexa/tools"/*.java -Xlint

echo Verificar se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro durante a compilação."
    exit 1
else
    echo "Compilação bem-sucedida."
fi

echo Criar o arquivo .jar com classe executável
cd out
echo "Main-Class: servalexa/main" > manifest.txt
jar cvfm main.jar manifest.txt servalexa/*.class servalexa/tools/*.class

echo Verificar se a criação do .jar foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro durante a criação do arquivo JAR."
    exit 1
else
    echo "Arquivo JAR criado com sucesso."
fi

echo Remover o arquivo manifest temporário
rm manifest.txt

#echo Gerando javadocs...
#cd ..
#javadoc -encoding UTF-8 -cp "$current_dir/dependencies.jar" -d docs -sourcepath src -subpackages servalexa

echo Testando compilacao:
java -jar out/main.jar -v
