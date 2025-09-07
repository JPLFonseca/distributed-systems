<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@ page import="servlets.*, database.*, java.math.BigDecimal, java.time.LocalDate, java.sql.ResultSet"%>
<head>
<meta charset="UTF-8">
<title>Gerente</title>
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
<%
String designacao = (String) request.getParameter("designacao");
%>
</head>
<body>

	<h1>Gerente</h1>

	<%if (designacao == null) { %>

	<div class="Clube">
		<label for="designacao">Designação do clube:</label>
		<br>
		<input type="text" id="designacao" name="designacao">
		<ul class="autocomplete-list" id="autocompleteList"></ul>
		<button id="confirmar" onclick="window.location.replace('Gerente.jsp?designacao='+document.getElementById('designacao').value)">Confirmar</button>
	</div>
	
	<br>
	<button onclick="window.location.replace('logout')">Logout</button>
	<script>
	var autocompleteData = [
		<%
		for (String clube : GestaoGerente.getDesignacoesClubes()) {
		%>
		'<%=clube%>',
		<%
		}
		%>
	];

    document.getElementById('designacao').addEventListener('input', function() {
        let inputValue = document.getElementById('designacao').value.toLowerCase();
        displayAutocompleteResults(inputValue);
    });

    function displayAutocompleteResults(inputValue) {
    	
    	let autocompleteList = document.getElementById('autocompleteList');
        autocompleteList.innerHTML = '';

        let filteredData = autocompleteData.filter(function(item) {
            return item.toLowerCase().includes(inputValue);
        });

        filteredData.forEach(function(item) {
            let listItem = document.createElement('li');
            listItem.classList.add('autocomplete-item');
            listItem.textContent = item;
            listItem.addEventListener('click', function() {
            	document.getElementById('designacao').value = item;
                autocompleteList.innerHTML = '';
            });
            autocompleteList.appendChild(listItem);
        });
    }
	
	</script>
		
	<% } else { %>
	<div id="Operacoes">
		<label for="operacao">Escolha a operação que pretende realizar: </label>
		<select id="operacao" onchange="atualizarPagina()">
			<option value="A">Atualizar dados de contato do clube</option>
        	<option value="B">Atualizar horário do clube</option>
        	<option value="C">Configurar no clube as salas e equipamentos - INATIVO</option>
        	<option value="D">Criar/gerir perfis dos utilizadores - INATIVO</option>
        	<option value="EE">Consultar os equipamentos menos utilizados</option>
        	<option value="F">Aceder ao mapa semanal de ocupação das salas - INATIVO</option>
        </select>
        <button id="Executar" onclick="document.getElementById('comando').value = document.getElementById('operacao').value; getResultados()">Executar</button>
	</div>
	<br>
	
	<div class="dados" id="Contactos">
		<h4>Contactos</h4>
			
		<label for="email">E-mail do clube:</label>
		<input type="email" id="email" name="email">
		<label for="tel">Nº de telemóvel do clube:</label>
		<input type="tel" id="telemovel" name="telemovel">
		<br>
	</div>
	
	<div class="dados" id="Horario" hidden="">	
		<h4>Horário</h4>
		<div id="horario"></div>
		<h4>Alterar</h4>
		<table id="addHorario">
			<tr>
				<th><label for="diaSemana">Dia da semana</label></th>
				<th><label for="horaAbertura">Hora de abertura</label></th>
				<th><label for="horaFecho">Hora de fecho</label></th>
				<th>Alteração</th>
			</tr>
			<tr>
				<td>
					<select id="diaSemana">
						<option value="Segunda">Segunda-Feira</option>
						<option value="Terca">Terça-Feira</option>
						<option value="Quarta">Quarta-Feira</option>
						<option value="Quinta">Quinta-Feira</option>
						<option value="Sexta">Sexta-Feira</option>
						<option value="Sabado">Sábado</option>
						<option value="Domingo">Domingo</option>
					</select>
				</td>
				<td><input type="time" id="horaAbertura"></td>
				<td><input type="time" id="horaFecho"></td>
				<td>
					<select id="opHorario" onchange="changeOpHorario()">
						<option value="add">Adicionar/Alterar</option>
						<option value="rem">Remover</option>
					</select>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="dados" id="Equipamentos" hidden="">
	</div>
	
	<div class="dados" id="Salas" hidden="">
		<h4>Salas</h4>
		<label for="numSala">Nº de sala</label>
		<br>
		<select id="numSala" onchange="atualizarFotoSala(); atualizarVideoSala();">
		</select>
		<br>
		<div id="fotoPreview">
			<img id="imgSala" width="320px" height="320px">
		</div>
		<input id="newImgSala" type="file" name="foto" accept="image/*">
		<div id="videoPreview">
			<video id="videoSala" width="320px" height="320px" controls autoplay></video>
		</div>
		<input id="newVideoSala" type="file" name="video" accept="video/*">
	</div>
	
	<br>
	<div id="resultados">
	</div>
	<input type="hidden" id="comando" name="comando" value="">
	<br>

	<button onclick="window.location.replace('Gerente.jsp')">Mudar de clube</button>
	<button onclick="window.location.replace('logout')">Logout</button>
	
	<script>
	
	let designacao = '<%=designacao%>';
	let numerosSala = [];
	
	function atualizarContactos(){
		
		let comando = 'W';

        let xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	contactos = xhr.responseText.split('|');
            	
            	let email = document.getElementById("email");
            	email.value = contactos[0];
            	
            	let telemovel = document.getElementById("telemovel");
            	telemovel.value = contactos[1];
            }
        };
        xhr.send();
	}
	
	function atualizarHorarios(){
		
		let comando = 'X';

        let xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	
            	let horario = document.getElementById("horario");
                horario.innerHTML = xhr.responseText;
            }
        };
        xhr.send();
	}
	
	function atualizarFotoSala() {
		
		let comando = 'Z';
		let numSala = document.getElementById("numSala").value;
		
		document.getElementById("imgSala").source = "FotoDown?numSala="+encodeURIComponent(numSala)+"&designacao="+encodeURIComponent(designacao);
		
		let xhr = new XMLHttpRequest();
		xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao)
				+"&numSala="+encodeURIComponent(numSala), true);
		xhr.responseType = "arraybuffer";
		
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4 && xhr.status == 200) {
				
				//document.getElementById("imgSala").source = "data:image/*;base64,"+xhr.response;
			}
		};
		xhr.send();
	}

	function atualizarVideoSala() {
		
		let comando = 'Y';
		let numSala = document.getElementById("numSala").value;

		document.getElementById("videoSala").source = "VideoDown?numSala="+encodeURIComponent(numSala)+"&designacao="+encodeURIComponent(designacao);
		
		let xhr = new XMLHttpRequest();
		xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao)
				+"&numSala="+encodeURIComponent(numSala), true);
		
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4 && xhr.status == 200) {
				
				//document.getElementById("videoSala").innerHTML = "<source src='data:video/*;base64,"+xhr.response+"'></source>";
			}
		};
		xhr.send();
	}
	
	function atualizarNumerosSala() {
		
		let comando = 'T';
		
        let xhr = new XMLHttpRequest();
		xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao), true);
		
		xhr.onreadystatechange = function() {
			if (xhr.readyState == 4 && xhr.status == 200) {
				
				numerosSala = xhr.responseText.split('|');
				let select = document.getElementById("numSala");
				select.innerHTML = "";
				
				for(numSala of numerosSala) {
                    let option = document.createElement("option");
                    option.value = numSala;
                    option.text = numSala;
                    select.appendChild(option);
				}
			}
		};
		xhr.send();
	}
	
	function changeOpHorario() {
		
		let operacao = document.getElementById("opHorario").value;
		
		if (operacao == "rem") {
			document.getElementById("horaAbertura").disabled = true;
			document.getElementById("horaFecho").disabled = true;
		}
		if (operacao == "add") {
			document.getElementById("horaAbertura").disabled = false;
			document.getElementById("horaFecho").disabled = false;
		}
	}
	
	function atualizarEquipamentos(){
		
		let comando = 'E';

        let xhr = new XMLHttpRequest();
        xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+encodeURIComponent(designacao), true);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
            	let equipamentos = document.getElementById("Equipamentos");
            	equipamentos.innerHTML = xhr.responseText;
            }
        };
        xhr.send();
	}

    function atualizarPagina(){
    	
    	let dados = document.getElementsByClassName("dados");
        for (d of dados) {
            d.hidden = true;
        }
    	
    	let operacao = document.getElementById("operacao").value;
    	
    	if(operacao == "A") {
    		document.getElementById("Contactos").hidden = false;
    		atualizarContactos();
    	}
    	else if(operacao == "B") {
    		document.getElementById("Horario").hidden = false;
    		atualizarHorarios();
    	}
    	else if(operacao == "C") {
    		document.getElementById("Salas").hidden = false;
    		atualizarNumerosSala();
    		atualizarFotoSala();
    		atualizarVideoSala();
    	}
    	else if(operacao == "EE") {
    		document.getElementById("Equipamentos").hidden = false;
    		atualizarEquipamentos();
    	}
    }
    
	function getResultados(){
		
		let designacao = "<%=designacao%>";
		let comando = document.getElementById("comando").value;
		let email = document.getElementById("email").value;
		let telemovel = document.getElementById("telemovel").value;
		let diaSemana = document.getElementById("diaSemana").value;
		let horaAbertura = document.getElementById("horaAbertura").value;
		let horaFecho = document.getElementById("horaFecho").value;
		let opHorario = document.getElementById("opHorario").value;
		let numSala = document.getElementById("numSala").value;
		let fotoSala = document.getElementById("newImgSala").files[0];
		let videoSala = document.getElementById("newVideoSala").files[0];
		
        let xhr = new XMLHttpRequest();
        
        if (comando == "C") {
        	let formData = new FormData();
        	formData.append('fotoSala', fotoSala);
        	formData.append('videoSala', videoSala);

        	xhr.open("POST", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+
        			encodeURIComponent(designacao)+"&numSala="+encodeURIComponent(numSala), true);
        	
        	xhr.onreadystatechange = function() {
        		if (xhr.readyState == 4 && xhr.status == 200) {
            		document.getElementById('resultados').innerHTML = xhr.responseText;
            		atualizarPagina();
        		}
        	};
        	xhr.send(formData);
        }
        else {
        	xhr.open("GET", "ServletGerente?comando="+encodeURIComponent(comando)+"&designacao="+
        			encodeURIComponent(designacao)+"&email="+encodeURIComponent(email)+"&telemovel="+
        			encodeURIComponent(telemovel)+"&diaSemana="+encodeURIComponent(diaSemana)+
        			"&horaAbertura="+encodeURIComponent(horaAbertura)+"&horaFecho="+encodeURIComponent(horaFecho)+
       		 		"&opHorario="+encodeURIComponent(opHorario), true);
        
        	xhr.onreadystatechange = function () {
            	if (xhr.readyState == 4 && xhr.status == 200) {
            		document.getElementById('resultados').innerHTML = xhr.responseText;
            		atualizarPagina();
            	}
        	};
        	xhr.send();
        }
	}
	
	window.onload = () => { atualizarPagina(); };
	</script>	

	<%} %>

</body>

</html>