<template>
	<div id="web-shop">
		<h1>Table</h1>
		<div id="products">
			<h3>{{ title }}</h3>

			<table id="tabela">
				<thead>
					<tr>
						<th></th>
						<th>Naziv</th>
						<th>Cena</th>
					</tr>
				</thead>

				<tr v-for="p in products">
					<td>{{ p.id }}</td>
					<td>{{ p.name }}</td>
					<td>{{ p.price }}</td>
				</tr>
			</table>

			<p><button class="dodaj" v-on:click="showForm()">Dodaj</button></p>

			<form id="forma" v-bind:hidden="mode == 'BROWSE'" @submit="createProduct($event)">
				<table>
					<tr>
						<td>Naziv</td>
						<td><input type="text" name="name" v-model="selectedProduct.name"></td>
					</tr>
					<tr>
						<td>Cena</td>
						<td><input type="number" name="price" v-model="selectedProduct.price"></td>
					</tr>
					<tr>
						<td><input type="submit" value="Pošalji"></td>
					</tr>
				</table>
				<p id="error">{{ error }}</p>
			</form>
		</div>
	</div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';


const title = ref("Product List");
const products = ref([]);
const mode = ref("BROWSE");
const error = ref("");
const selectedProduct = ref({ name: "", price: 0 });

onMounted(() => {
	loadProducts();
})

function loadProducts() {
	axios.get('http://localhost:8080/WebShopAppREST/rest/products/')
		.then(response => {
			(products.value = response.data)
		});
}

function showForm() {
	mode.value = "CREATE";
	selectedProduct.value = { id: '', name: null, price: null }

}

function createProduct(event) {
	this.error = ""
	if (!this.selectedProduct.name || !this.selectedProduct.price) {
		this.error = "Unesite naziv i cenu";
		event.preventDefault();
		return;
	}
	if (this.mode == 'CREATE') {
		event.preventDefault();
		console.log(this.selectedProduct)
		axios.post('http://localhost:8080/WebShopAppREST/rest/products', this.selectedProduct)
			.then((response) => {
				alert('Novi proizvod uspešno kreiran')
				this.mode = 'BROWSE'
				this.products.push(response.data)
			})
	}
}



</script>

<style scoped>
#web-shop {
	width: 600px;
}

#tabela {
	color: white;
	background-color: #333;
	border-collapse: collapse;
	width: 100%;
}

#tabela th,
#tabela td {
	padding: 8px;
	text-align: left;
	border-bottom: 1px solid #ddd;
}

#tabela th {
	background-color: #555;
}

#tabela tr:nth-child(even) {
	background-color: #444;
}

#tabela tr:hover {
	background-color: #777;
}

.dodaj {
	padding: 8px;
	border: none;
	border-radius: 0.25rem;
	background-color: #007bff;
	color: white;
}
</style>