import * as functions from "firebase-functions";

export const getMercadoPlayers = functions.https.onRequest(
  (request, response) => {
    const mercadoPlayers = [
      // Porteros (6)
      {name: "Marc-André ter Stegen", position: "Goalkeeper", overall: 89},
      {name: "Alisson Becker", position: "Goalkeeper", overall: 89},
      {name: "Mike Maignan", position: "Goalkeeper", overall: 87},
      {name: "Jan Oblak", position: "Goalkeeper", overall: 88},
      {name: "Gregor Kobel", position: "Goalkeeper", overall: 85},
      {name: "Gianluigi Donnarumma", position: "Goalkeeper", overall: 87},

      // Defensores (14)
      {name: "Ronald Araújo", position: "Defender", overall: 86},
      {name: "William Saliba", position: "Defender", overall: 84},
      {name: "Theo Hernández", position: "Defender", overall: 85},
      {name: "Achraf Hakimi", position: "Defender", overall: 85},
      {name: "Alphonso Davies", position: "Defender", overall: 84},
      {name: "Dayot Upamecano", position: "Defender", overall: 83},
      {name: "Jules Koundé", position: "Defender", overall: 84},
      {name: "Giovanni Di Lorenzo", position: "Defender", overall: 84},
      {name: "Jeremie Frimpong", position: "Defender", overall: 83},
      {name: "Antonio Rüdiger", position: "Defender", overall: 86},
      {name: "Raphaël Varane", position: "Defender", overall: 84},
      {name: "Matthijs de Ligt", position: "Defender", overall: 85},
      {name: "Milan Škriniar", position: "Defender", overall: 84},
      {name: "Benjamin Pavard", position: "Defender", overall: 83},

      // Centrocampistas (16)
      {name: "Frenkie de Jong", position: "Midfielder", overall: 86},
      {name: "Martin Ødegaard", position: "Midfielder", overall: 87},
      {name: "Sandro Tonali", position: "Midfielder", overall: 84},
      {name: "Warren Zaïre-Emery", position: "Midfielder", overall: 78},
      {name: "Jamal Musiala", position: "Midfielder", overall: 86},
      {name: "Florian Wirtz", position: "Midfielder", overall: 85},
      {name: "Fabián Ruiz", position: "Midfielder", overall: 83},
      {name: "Alejandro Grimaldo", position: "Midfielder", overall: 82},
      {name: "Adrien Rabiot", position: "Midfielder", overall: 84},
      {name: "Eduardo Camavinga", position: "Midfielder", overall: 84},
      {name: "Federico Valverde", position: "Midfielder", overall: 87},
      {name: "Ismaël Bennacer", position: "Midfielder", overall: 83},
      {name: "Nicolò Barella", position: "Midfielder", overall: 86},
      {name: "Luka Modrić", position: "Midfielder", overall: 87},
      {name: "Joshua Kimmich", position: "Midfielder", overall: 88},
      {name: "Enzo Fernández", position: "Midfielder", overall: 83},

      // Delanteros (16)
      {name: "Kylian Mbappé", position: "Forward", overall: 91},
      {name: "Robert Lewandowski", position: "Forward", overall: 90},
      {name: "Lautaro Martínez", position: "Forward", overall: 88},
      {name: "Victor Osimhen", position: "Forward", overall: 88},
      {name: "Mohamed Salah", position: "Forward", overall: 89},
      {name: "Heung-min Son", position: "Forward", overall: 87},
      {name: "Leroy Sané", position: "Forward", overall: 85},
      {name: "Randal Kolo Muani", position: "Forward", overall: 84},
      {name: "João Félix", position: "Forward", overall: 83},
      {name: "Rodrygo", position: "Forward", overall: 85},
      {name: "Ansu Fati", position: "Forward", overall: 78},
      {name: "Marcus Rashford", position: "Forward", overall: 86},
      {name: "Neymar Jr", position: "Forward", overall: 89},
      {name: "Karim Adeyemi", position: "Forward", overall: 80},
      {name: "Jonathan David", position: "Forward", overall: 82},
      {name: "Ángel Di María", position: "Forward", overall: 83},
    ];

    response.json({data: {players: mercadoPlayers}});
  }
);
