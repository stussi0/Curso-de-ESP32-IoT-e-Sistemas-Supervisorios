#define filtro 16384

// Variáveis globais para as frequências de corte (em Hz)
float freq_corte1 = 0.5;   // Frequência de corte 1
float freq_corte2 = 2.0;   // Frequência de corte 2  
float freq_corte3 = 5.0;   // Frequência de corte 3

// Coeficientes dos filtros
float alpha1, alpha2, alpha3;

// Estados dos filtros
float filtered1 = 0, filtered2 = 0, filtered3 = 0;

// Variáveis para cálculo do tempo
unsigned long lastTime = 0;
float dt = 0;

void setup() {
  Serial.begin(115200);
  analogReadResolution(12);
  
  // Inicializa coeficientes dos filtros (serão atualizados no primeiro loop)
  alpha1 = alpha2 = alpha3 = 0.1;
  
  Serial.println(34);
  Serial.println(15);
}

void calcularCoeficientesFiltro() {
  // Calcula os coeficientes alpha baseados nas frequências de corte e no dt
  // Fórmula: alpha = (2 * π * fc * dt) / (2 * π * fc * dt + 1)
  if (dt > 0) {
    float omega1 = 2 * PI * freq_corte1 * dt;
    float omega2 = 2 * PI * freq_corte2 * dt;
    float omega3 = 2 * PI * freq_corte3 * dt;
    
    alpha1 = omega1 / (omega1 + 1);
    alpha2 = omega2 / (omega2 + 1);
    alpha3 = omega3 / (omega3 + 1);
  }
}

float aplicarFiltroPasseBaixa(float input, float &estado, float alpha) {
  // Filtro IIR de primeira ordem (passe-baixa)
  estado = alpha * input + (1 - alpha) * estado;
  return estado;
}

void loop() {
  // Calcula delta time
  unsigned long currentTime = micros();
  dt = (currentTime - lastTime) / 1000000.0; // Converter para segundos
  lastTime = currentTime;
  
  // Atualiza coeficientes dos filtros se dt for válido
  if (dt > 0 && dt < 1.0) { // Evita valores inválidos
    calcularCoeficientesFiltro();
  }
  
  double sA0 = -30.30e-3;
  double sB0 = 52.01e-4;
  double sC0 = -182.69e-7;
  float vsinal;
  float tens;
  double somatoria = 0;
  
  // Aquisição dos dados
  for(int i = 0; i < filtro; i++) {
    int sensorValue = analogRead(2);
    tens = (float)sensorValue / 1240.9; // Tensão em V
    somatoria += tens;
  }
  
  vsinal = somatoria / (double)filtro;
  tens = (((float)3.3 - vsinal) * 9820.0) / vsinal;
  double temper = (1.0 / (sA0 + (sB0 * log(tens)) + (sC0 * pow(log(tens), 3)))) - 273.15;
  
  // Aplica os três filtros em cascata
  float temp_filtrada1 = aplicarFiltroPasseBaixa(temper, filtered1, alpha1);
  float temp_filtrada2 = aplicarFiltroPasseBaixa(temp_filtrada1, filtered2, alpha2);
  float temp_filtrada3 = aplicarFiltroPasseBaixa(temp_filtrada2, filtered3, alpha3);
  
  // Envia todos os valores (original + filtrados)
  Serial.print("Original: ");
  Serial.print(temper, 4);
  Serial.print(" | Filtro1(");
  Serial.print(freq_corte1);
  Serial.print("Hz): ");
  Serial.print(temp_filtrada1, 4);
  Serial.print(" | Filtro2(");
  Serial.print(freq_corte2);
  Serial.print("Hz): ");
  Serial.print(temp_filtrada2, 4);
  Serial.print(" | Filtro3(");
  Serial.print(freq_corte3);
  Serial.print("Hz): ");
  Serial.println(temp_filtrada3, 4);

  delay(100);
}

// Funções para modificar as frequências em tempo de execução
void setFrequenciaCorte1(float freq) {
  freq_corte1 = freq;
}

void setFrequenciaCorte2(float freq) {
  freq_corte2 = freq;
}

void setFrequenciaCorte3(float freq) {
  freq_corte3 = freq;
}