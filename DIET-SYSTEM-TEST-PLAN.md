# Plano de Testes - Sistema de Gestão Dietética

## 🏗️ Visão Macro do Sistema

### 3 Pilares Principais
1. **Planejamento** - Criar dietas, usar presets, montar calendário
2. **Execução** - Registrar consumo real, customizar refeições
3. **Análise** - Comparar planejado vs realizado, calcular macros

---

## 🧪 Casos de Teste (BDD Style)

### 🍽️ Módulo: Refeições

#### ✅ Caso 1 — Criar refeição customizada
**DADO** que existem alimentos cadastrados
**QUANDO** eu crio uma refeição com múltiplos alimentos e quantidades
**ENTÃO** a refeição deve ser salva com seus itens corretamente
**E** deve ser possível calcular suas calorias totais

#### ✅ Caso 2 — Criar preset de refeição
**DADO** uma refeição válida
**QUANDO** eu salvo como preset
**ENTÃO** deve ser possível reutilizar esse preset futuramente

#### ✅ Caso 3 — Criar plano alimentar
**DADO** um paciente existente
**QUANDO** eu crio um plano alimentar com data inicial e final
**ENTÃO** o plano deve ser associado ao paciente

#### ✅ Caso 4 — Planejar refeição com preset
**DADO** um plano alimentar existente
**E** um preset de refeição
**QUANDO** eu adiciono uma refeição em um dia específico usando o preset
**ENTÃO** a refeição deve ser registrada como planejada

#### ✅ Caso 5 — Planejar refeição customizada
**DADO** um plano alimentar existente
**QUANDO** eu adiciono uma refeição customizada em um dia
**ENTÃO** ela deve ser salva sem depender de preset

#### ❌ Caso 6 — Evitar conflito (regra importante)
**DADO** uma refeição planejada
**QUANDO** eu tento associar ao mesmo tempo preset e refeição customizada
**ENTÃO** o sistema deve rejeitar a operação

---

### 📅 Módulo: Planejamento (DietPlan)

#### ✅ Caso 7 — Criar plano alimentar
**DADO** um paciente existente
**QUANDO** eu crio um plano alimentar com data inicial e final
**ENTÃO** o plano deve ser associado ao paciente

#### ✅ Caso 8 — Planejar refeição com preset
**DADO** um plano alimentar existente
**E** um preset de refeição
**QUANDO** eu adiciono uma refeição em um dia específico usando o preset
**ENTÃO** a refeição deve ser registrada como planejada

#### ✅ Caso 9 — Planejar refeição customizada
**DADO** um plano alimentar existente
**QUANDO** eu adiciono uma refeição customizada em um dia
**ENTÃO** ela deve ser salva sem depender de preset

---

### 📊 Módulo: Consumo Real (FoodLog)

#### ✅ Caso 7 — Registrar consumo baseado em preset
**DADO** um paciente
**E** um preset de refeição
**QUANDO** eu registro consumo usando esse preset
**ENTÃO** o consumo deve ser salvo corretamente

#### ✅ Caso 8 — Registrar consumo customizado
**DADO** um paciente
**QUANDO** eu registro uma refeição manual com alimentos e quantidades
**ENTÃO** o sistema deve salvar corretamente

#### ✅ Caso 9 — Alterar consumo real
**DADO** um consumo registrado
**QUANDO** eu altero a quantidade de um alimento
**ENTÃO** os valores nutricionais devem ser recalculados

---

### 📈 Módulo: Comparação (ESSENCIAL)

#### ✅ Caso 10 — Comparar planejado vs realizado
**DADO** um plano alimentar
**E** um consumo real no mesmo dia
**QUANDO** eu solicito comparação
**ENTÃO** o sistema deve retornar:
- calorias planejadas
- calorias consumidas
- diferença

#### 🔥 Caso 11 — Dia sem planejamento
**DADO** um consumo real em um dia sem plano
**QUANDO** eu consulto o histórico
**ENTÃO** o sistema deve permitir e marcar como "não planejado"

#### ✅ Caso 12 — Dia planejado sem consumo
**DADO** um dia planejado
**QUANDO** não há consumo registrado
**ENTÃO** o sistema deve indicar ausência de execução

---

### 🧮 Caso 13 — Paciente inexistente
**DADO** paciente inexistente
**QUANDO** eu tento criar plano ou log para paciente inexistente
**ENTÃO** o sistema deve retornar erro

---

### 🧱 Caso 14 — Cálculo nutricional
**DADO** uma refeição com múltiplos alimentos
**QUANDO** eu calculo os nutrientes
**ENTÃO** deve retornar:
- calorias totais
- proteínas
- carboidratos
- gorduras

---

## 🧪 3. Tipos de Teste

### 🔹 Unitários
- **Cálculo de calorias**
- **Regras de validação**
- **Lógica de negócio isolada**

### 🔹 Integração
- **Salvar dieta**
- **Salvar consumo**
- **Integração entre módulos**

### 🔹 End-to-End (Postman)
- **Fluxo completo**
- **Autenticação JWT**
- **API endpoints**

---

## ⚠️ 4. ERROS que você deve evitar agora

### ❌ Criar teste baseado em controller
- Evite testes que dependam diretamente de controllers
- Foque em comportamento do sistema

### ❌ Testar banco direto
- Não acesse o banco diretamente nos testes
- Use repositories e services

### ❌ Misturar regra de negócio com framework
- Separe claramente lógica de negócio
- Não teste detalhes de implementação do Spring

### 👉 Foco é: comportamento do sistema
- Teste o que o sistema FAZ, não como ele faz
- Valide regras de negócio
- Verifique fluxos completos

---

## 🎯 5. Estratégia de Implementação

### Fase 1: Fundação
1. **Modelos de Domínio** - Meal, Food, DietPlan, FoodLog
2. **Regras de Negócio** - Cálculo calórico, validações
3. **Repositories** - Camada de dados

### Fase 2: Serviços
1. **MealService** - Gerenciar refeições e presets
2. **DietPlanService** - Planejamento alimentar
3. **FoodLogService** - Registro de consumo
4. **NutritionService** - Cálculos nutricionais

### Fase 3: API
1. **Controllers** - Endpoints REST
2. **DTOs** - Transfer objects
3. **Validação** - Input validation

### Fase 4: Testes
1. **Unitários** - Lógica de negócio
2. **Integração** - Serviços e repositories
3. **E2E** - Fluxos completos via API

---

## 🧪 6. Exemplos de Testes Unitários

```java
@Test
void deveCalcularCaloriasCorretamente() {
    // DADO
    Food arroz = new Food("Arroz", 130);
    Food feijao = new Food("Feijão", 80);
    Meal meal = new Meal();
    meal.addFood(arroz, 100); // 100g
    meal.addFood(feijao, 50);  // 50g
    
    // QUANDO
    int totalCalories = meal.calculateTotalCalories();
    
    // ENTÃO
    assertEquals(165, totalCalories);
}

@Test
void deveRejeitarRefeicaoSemAlimentos() {
    // DADO
    Meal meal = new Meal();
    
    // QUANDO/ENTÃO
    assertThrows(ValidationException.class, () -> {
        meal.validate();
    });
}
```

---

## 📋 7. Exemplos de Testes de Integração

```java
@SpringBootTest
@Transactional
class MealServiceIntegrationTest {
    
    @Test
    void deveCriarRefeicaoComSucesso() {
        // DADO
        CreateMealRequest request = new CreateMealRequest();
        request.setName("Almoço");
        request.addFood(new FoodItem("Arroz", 100));
        
        // QUANDO
        Meal created = mealService.create(request);
        
        // ENTÃO
        assertNotNull(created.getId());
        assertEquals("Almoço", created.getName());
        assertEquals(1, created.getFoodItems().size());
    }
}
```

---

## 🌐 8. Exemplos de Testes E2E (Postman)

```json
{
  "info": {
    "name": "Diet System E2E Tests"
  },
  "item": [
    {
      "name": "Fluxo Completo: Criar Plano e Registrar Consumo",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.test('Plano criado com sucesso', function() {",
              "    pm.response.to.have.status(201);",
              "});",
              "",
              "var planoId = pm.response.json().id;",
              "pm.collectionVariables.set('plano_id', planoId);"
            ]
          }
        }
      ]
    }
  ]
}
```

---

## 🎯 Próximos Passos

1. **Definir estrutura de domínio**
2. **Implementar regras de negócio**
3. **Criar camada de serviços**
4. **Desenvolver API REST**
5. **Implementar testes em camadas**
6. **Configurar CI/CD para testes automatizados**

---

**Status**: Planejamento Completo  
**Foco**: Comportamento do Sistema  
**Metodologia**: BDD + Test Pyramid
