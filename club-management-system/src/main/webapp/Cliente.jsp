<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@ page import="servlets.*, database.*, java.math.BigDecimal, java.time.LocalDate, java.sql.ResultSet"%>
<head>
<meta charset="UTF-8">
<title>Cliente</title>
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
	<div class="Operacoes">
		<label for="Operacao">Escolha a operação que pretende realizar: </label>
		<select id="Operacao" onchange="atualizarPagina()">
			<option value="A">Atualizar perfil</option>
        	<option value="B">Subscrever atividades de individuais do seu PT</option>
        	<option value="C">Aderir a atividades de grupo</option>
        	<option value="D">Ver atividades da semana</option>
        	<option value="E">Ver recomendações do seu PT</option>
        	<option value="F">Ver atividades individuais do seu PT</option>
        	<option value="G">Ver atividades em grupo</option>
        </select>
        <button formnovalidate="formnovalidate" id="Executar" onclick="document.getElementById('Comando').value = document.getElementById('Operacao').value; getResultados()">Executar</button>
	</div>
	<br>
	
	
	<div class="AtualizaPerfil">
    <label for="Email">Email:</label>
    <input type="email" id="Email" placeholder="Coloque aqui o seu novo email" disabled>

    <label for="telemovel">Mobile Phone:</label>
    <input type="tel" id="telemovel" placeholder="Coloque aqui o seu novo numero de telemovel" disabled>

    <label for="Patolgoia">Patologia:</label>
    <input id="Patologia" placeholder="Nome da patologia" disabled>

    <label for="dataI">Data de Inicio:</label>
    <input type="date" id="dataInicio" disabled>

    <label for="dataF">Data de Final:</label>
    <input type="date" id="dataFim" disabled>

    <label for="Objetivo">Objetivo:</label>
    <input id="Objetivo" placeholder="Introduza o seu obejtivo" disabled></div><br>
    
	<div class="Atividades">
		<label for="escolheAtividade">Escolha uma atividade para se inscrever: </label>
		<input id="Atividade" disabled>
		<input type="date" id="diaAtividade" disabled>
		<input type="time" id="inicioAtividade" disabled>
		<input type="time" id="fimAtividade" disabled>
	</div>
	<br>
	<div id="resultados">
	</div>
	<input type="hidden" id="Comando" name="Comando" value="">
	
	<script>
	<% String uIDrecebido = (String) request.getAttribute("uID");%>
	
	var uID = '<%= uIDrecebido %>';
    console.log(uID + ' Ç');
	var autocompleteData = [];
	var escaloes = [];
	var atividades = [];
	
	
	function atualizarAtividades(){
		
		var comando = 'G';

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

    
    
    
    function atualizarPagina(){
    	
    	//atualizarAtividades();
    	//atualizarNomes();
    	
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
    	
    	
    	if(operacao === "A"){
    		document.getElementById("Email").disabled = false;
    		document.getElementById("Patologia").disabled = false;
    		document.getElementById("dataInicio").disabled = false;
    		document.getElementById("dataFim").disabled = false;
    		document.getElementById("telemovel").disabled = false;
    		document.getElementById("Objetivo").disabled = false;
    	}
    	else if(operacao === "B" || operacao === "C"){
    		document.getElementById("diaAtividade").disabled = false;
    		document.getElementById("inicioAtividade").disabled = false;
    		document.getElementById("fimAtividade").disabled = false;
    		document.getElementById("Atividade").disabled = false;
    	}
    	
    	
    }
    
	function getResultados(){
		
		var comando = document.getElementById("Comando").value;
		var atividade = document.getElementById("Atividade").value;
		var diaAtividade = document.getElementById("diaAtividade").value;
		var inicioAtividade = document.getElementById("inicioAtividade").value;
		var fimAtividade = document.getElementById("fimAtividade").value;
		var email = document.getElementById("Email").value;
		var dataInicio = document.getElementById("dataInicio").value;
		var dataFim = document.getElementById("dataFim").value;
		var telemovel = document.getElementById("telemovel").value;
		var objetivo = document.getElementById("Objetivo").value;
		var patologia = document.getElementById("Patologia").value;
		
		console.log(comando);

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletCliente?comando="+encodeURIComponent(comando)+"&uID="+encodeURIComponent(uID)+"&Atividade="+encodeURIComponent(atividade)+"&diaAtividade="+encodeURIComponent(diaAtividade)+"&inicioAtividade="+encodeURIComponent(inicioAtividade)+"&fimAtividade="+encodeURIComponent(fimAtividade)+"&email="+encodeURIComponent(email)+"&telemovel="+encodeURIComponent(telemovel)+"&=patologia"+encodeURIComponent(patologia)+"&dataInicio="+encodeURIComponent(dataInicio)+"&dataFim="+encodeURIComponent(dataFim)+"&objetivo="+encodeURIComponent(objetivo), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	document.getElementById('resultados').innerHTML = xhr.responseText;
            }
        };
        xhr.send();
	}
	</script>	


</body>

</html>