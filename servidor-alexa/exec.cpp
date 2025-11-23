#include <iostream>
#include <cstring>

char* stringToCharPointer(const std::string& str);

char* stringToCharPointer(const std::string& str) {
    char* charPtr = new char[str.length() + 1];
    std::strcpy(charPtr, str.c_str());
    return charPtr;
}

int main(int argc, char** argv) {
	std::string concatenatedArgs;
	concatenatedArgs += "java -cp \"./out/:./dependencies.jar\" servalexa.main ";
    for (int i = 1; i < argc; ++i) {
        concatenatedArgs += argv[i];
        if (i < argc - 1) {
            concatenatedArgs += " ";
        }
    }
    char* args = stringToCharPointer(concatenatedArgs);
    int result = std::system(args);
    // Verificação da instalação do java com o comando java utilizado e verificação do código de saída
    if (result != 0) {
        std::cerr << "Java não está instalado no sistema.\nresponse: " << result << "\n";
        std::cout << "Por favor, baixe o Java em: https://www.java.com/en/download/\n";
        std::system("start https://www.java.com/en/download/");
        return 1;
    }
    return 0;
}
