<script setup>
import { ref } from 'vue';
import TheWelcome from '../components/TheWelcome.vue'

const ime = ref("Milos Popovic")
const brojac = ref(0)
const korisnici = ref([
  {
    id: 1,
    ime: "pera",
    prezime: "petrovic",
    admin: false
  },{
    id: 2,
    ime: "Nikola",
    prezime: "Pejanovic",
    admin: true
  },{
    id: 3,
    ime: "Minja",
    prezime: "Profesor",
    admin: true
  }
])

function povecaj() {
  brojac.value += 1;
}

function test() {
  alert("evo ga")
}

</script>

<template>
  <main>
    <h1>Vase ime je {{ ime }}</h1>

    <input type="text" v-model="ime">


    <h1 @click="test">Testasds</h1>
    <h2>{{ brojac }}</h2>
    <button v-on:click="povecaj">Povecaj v-on</button>
    <button @click="povecaj">Povecaj @click</button>
    <p>Lorem ipsum dolor, sit amet consectetur adipisicing elit. Ipsa, quasi.</p>

    <div>
      <div v-for="korinsik in korisnici">
        <div v-if="korinsik.admin == false">
          <ul>
            <li>{{ korinsik.ime }}</li>
            <li :class="korinsik.admin?'bold':'italic'">{{ korinsik.prezime }}</li>
          </ul>
          <hr>
        </div>
      </div>
    </div>
  </main>
</template>



