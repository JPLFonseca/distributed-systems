<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@ page import="servlets.*, database.*, java.math.BigDecimal, java.time.LocalDate, java.sql.ResultSet"%>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<style>
	table, th, td {
	  border:1px solid black;
	}
    .autocomplete-list {
        position: absolute;
        z-index: 1;
        list-style: none;
        padding: 0;
        margin: 0;
        border: 1px solid #ccc;
        max-height: 150px;
        overflow-y: auto;
    }


    .autocomplete-item {
        padding: 8px;
        cursor: pointer;
        background-color: #F1F1F1;
    }

    .autocomplete-item:hover {
        background-color: #e0e0e0;
    }
</style>
<body>
	<h1>PT</h1>
	<button onclick="window.location.replace('logout')">Logout</button>
	<br>
	<br>
	<div class="Operacoes">
		<label for="Operacao">Escolha a operção que pretende realizar: </label>
		<select id="Operacao" onchange="atualizarPagina()">
			<option value="A">Visualizar as próprias manchas</option>
        	<option value="B">Consultar manchas por escalão</option>
        	<option value="C">Publicar mancha</option>
        	<option value="D">Confirmar/cancelar mancha</option>
        	<option value="E">Procurar ficha de cliente</option>
        	<option value="F">Agendar cliente numa mancha</option>
        	<option value="G">Recomendar equipamento a cliente</option>
        </select>
        <button formnovalidate="formnovalidate" id="Executar" onclick="document.getElementById('Comando').value = document.getElementById('Operacao').value; getResultados()">Executar</button>
	</div>
	<br>
	<div class="Nome">
		<input maxlength="100" size="50" type="text" id="NomeCliente" pattern="[a-zA-Z '\x22ÂÁÉÍÓÚàáãâéêíóõôúçñ\-]{5,100}" placeholder="Nome do cliente" disabled>
		<ul class="autocomplete-list" id="autocompleteList"></ul>
	</div>
	<br>
	<div class="Manchas">
		<label for="Dia">Detalhes de manchas: </label>
		<input type="date" id="DiaMancha" disabled>
		<input type="time" id="HoraInicio" disabled>
		<input type="time" id="HoraFim" disabled>
		<select id="Atividades" disabled>
        </select>
		<select id="Escaloes" disabled>
        </select>
        <select id="Confirmacao" disabled>
        	<option value="1">Confirmar</option>
        	<option value="0">Cancelar</option>
        </select>
	</div>
	<br>
	<div class="Recomendacoes">
		<label for="InicioRecomendacao">Detalhes de recomendacoes: </label>
		<input type="date" id="InicioRecomendacao" disabled>
		<input type="date" id="FimRecomendacao" disabled>
		<select id="Equipamentos" disabled>
        </select>
		<select id="Recomendacao" disabled>
        	<option value="1">Aconselhar</option>
        	<option value="0">Desaconselhar</option>
        </select>
	</div>
	<br>
	<div id="resultados">
	</div>
	<input type="hidden" id="Comando" name="Comando" value="">
	
	<script>
	<% String numPTrecebido = (String) request.getAttribute("numPT");%>
	
	var numPT = '<%= numPTrecebido %>';
    console.log(numPT + ' Ç');
	var autocompleteData = [];
	var escaloes = [];
	var atividades = [];
	
	function atualizarEquipamentos(){
		
		var comando = 'W';

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletPT?comando="+encodeURIComponent(comando), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	equipamentos = xhr.responseText.split('|');
            	
            	var select = document.getElementById("Equipamentos");
                select.innerHTML = "";
                
                for (var i = 0; i < equipamentos.length; i++) {
                    var option = document.createElement("option");
                    option.value = equipamentos[i];
                    option.text = equipamentos[i];
                    select.appendChild(option);
                }
            }
        };
        xhr.send();
	}
	
	function atualizarAtividades(){
		
		var comando = 'X';

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletPT?comando="+encodeURIComponent(comando), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	atividades = xhr.responseText.split('|');
            	
            	var select = document.getElementById("Atividades");
                select.innerHTML = "";
                
                for (var i = 0; i < escaloes.length; i++) {
                    var option = document.createElement("option");
                    option.value = atividades[i];
                    option.text = atividades[i];
                    select.appendChild(option);
                }
            }
        };
        xhr.send();
	}
	
	function atualizarEscaloes(){
		
		var comando = 'Y';

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletPT?comando="+encodeURIComponent(comando), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	escaloes = xhr.responseText.split('|');
            	
            	var select = document.getElementById("Escaloes");
                select.innerHTML = "";
                
                for (var i = 0; i < escaloes.length; i++) {
                    var option = document.createElement("option");
                    option.value = escaloes[i];
                    option.text = escaloes[i];
                    select.appendChild(option);
                }
            }
        };
        xhr.send();
	}

    function atualizarNomes(){
    	
    	var comando = 'Z';

		console.log(numPT);

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletPT?comando="+encodeURIComponent(comando)+"&numPT="+encodeURIComponent(numPT), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	autocompleteData = xhr.responseText.split('|');
            }
        };
        xhr.send();
    }
    
    document.getElementById('NomeCliente').addEventListener('input', function() {
        var inputValue = document.getElementById('NomeCliente').value.toLowerCase();
        displayAutocompleteResults(inputValue);
    });

    function displayAutocompleteResults(inputValue) {
    	
    	var autocompleteList = document.getElementById('autocompleteList');
        autocompleteList.innerHTML = '';

        var filteredData = autocompleteData.filter(function(item) {
            return item.toLowerCase().includes(inputValue);
        });

        filteredData.forEach(function(item) {
            var listItem = document.createElement('li');
            listItem.classList.add('autocomplete-item');
            listItem.textContent = item;
            listItem.addEventListener('click', function() {
            	document.getElementById('NomeCliente').value = item;
                autocompleteList.innerHTML = '';
            });
            autocompleteList.appendChild(listItem);
        });
    }
    
    function atualizarPagina(){
    	
    	atualizarAtividades();
    	atualizarNomes();
    	atualizarEscaloes();
    	atualizarEquipamentos();
    	
    	var inputs = document.getElementsByTagName("input");
    	var selects = document.getElementsByTagName("select");
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].disabled = true;
        }
        for (var i = 0; i < selects.length; i++) {
        	selects[i].disabled = true;
        }
        document.getElementById("Operacao").disabled = false;
    	
    	var operacao = document.getElementById("Operacao").value;
    	console.log(operacao);
    	
    	if(operacao === "B"){
    		document.getElementById("Escaloes").disabled = false;
    	}
    	else if(operacao === "C"){
    		document.getElementById("DiaMancha").disabled = false;
    		document.getElementById("HoraInicio").disabled = false;
    		document.getElementById("HoraFim").disabled = false;
    		document.getElementById("Atividades").disabled = false;
    		document.getElementById("Escaloes").disabled = false;
    	}
    	else if(operacao === "D"){
    		document.getElementById("DiaMancha").disabled = false;
    		document.getElementById("HoraInicio").disabled = false;
    		document.getElementById("HoraFim").disabled = false;
    		document.getElementById("Confirmacao").disabled = false;
    	}
    	else if(operacao === "E"){
    		document.getElementById("NomeCliente").disabled = false;
    	}
    	else if(operacao === "F"){
    		document.getElementById("NomeCliente").disabled = false;
    		document.getElementById("DiaMancha").disabled = false;
    		document.getElementById("HoraInicio").disabled = false;
    		document.getElementById("HoraFim").disabled = false;
    	}
    	else if(operacao === "G"){
    		document.getElementById("NomeCliente").disabled = false;
    		document.getElementById("Equipamentos").disabled = false;
    		document.getElementById("InicioRecomendacao").disabled = false;
    		document.getElementById("FimRecomendacao").disabled = false;
    		document.getElementById("Recomendacao").disabled = false;
    	}
    	
    }
    
	function getResultados(){
		
		var comando = document.getElementById("Comando").value;
		var nomeCliente = document.getElementById("NomeCliente").value;
		var escalao = document.getElementById("Escaloes").value;
		var atividade = document.getElementById("Atividades").value;
		var diaMancha = document.getElementById("DiaMancha").value;
		var horaInicio = document.getElementById("HoraInicio").value;
		var horaFim = document.getElementById("HoraFim").value;
		var confirmacao = document.getElementById("Confirmacao").value;
		var inicioRecomendacao = document.getElementById("InicioRecomendacao").value;
		var fimRecomendacao = document.getElementById("FimRecomendacao").value;
		var equipamento = document.getElementById("Equipamentos").value;
		var recomendacao = document.getElementById("Recomendacao").value;

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletPT?comando="+encodeURIComponent(comando)+"&numPT="+encodeURIComponent(numPT)+"&nomeCliente="+encodeURIComponent(nomeCliente)+"&escalao="+encodeURIComponent(escalao)+"&diaMancha="+encodeURIComponent(diaMancha)+"&horaInicio="+encodeURIComponent(horaInicio)+"&horaFim="+encodeURIComponent(horaFim)+"&atividade="+encodeURIComponent(atividade)+"&confirmacao="+encodeURIComponent(confirmacao)+"&inicioRecomendacao="+encodeURIComponent(inicioRecomendacao)+"&fimRecomendacao="+encodeURIComponent(fimRecomendacao)+"&equipamento="+encodeURIComponent(equipamento)+"&recomendacao="+encodeURIComponent(recomendacao), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	document.getElementById('resultados').innerHTML = xhr.responseText;
            }
        };
        xhr.send();
	}
	
	window.onload = () => { atualizarPagina(); };
	</script>	


</body>

</html>