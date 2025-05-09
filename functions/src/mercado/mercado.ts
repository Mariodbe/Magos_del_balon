import * as functions from "firebase-functions";

export const getMercadoPlayers = functions.https.onRequest(
  (request, response) => {
    const mercadoPlayers = [
      // Porteros (6)
      {
        name: "Marc-André ter Stegen",
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Mike Maignan",
        position: "Goalkeeper",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Jan Oblak",
        position: "Goalkeeper",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Gregor Kobel",
        position: "Goalkeeper",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Gianluigi Donnarumma",
        position: "Goalkeeper",
        overall: 87,
        precio: 7221000,
      },

      // Defensores (14)
      {
        name: "Ronald Araújo",
        position: "Defender",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "William Saliba",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Theo Hernández",
        position: "Defender",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Achraf Hakimi",
        position: "Defender",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Alphonso Davies",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Dayot Upamecano",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Giovanni Di Lorenzo",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Jeremie Frimpong",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defender",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Raphaël Varane",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Matthijs de Ligt",
        position: "Defender",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Milan Škriniar",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Benjamin Pavard",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },

      // Centrocampistas (16)
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Martin Ødegaard",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Sandro Tonali",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Warren Zaïre-Emery",
        position: "Midfielder",
        overall: 78,
        precio: 6474000,
      },
      {
        name: "Jamal Musiala",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Florian Wirtz",
        position: "Midfielder",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Fabián Ruiz",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Alejandro Grimaldo",
        position: "Midfielder",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Adrien Rabiot",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Eduardo Camavinga",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Federico Valverde",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Ismaël Bennacer",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Nicolò Barella",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Luka Modrić",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Joshua Kimmich",
        position: "Midfielder",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Enzo Fernández",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },

      // Delanteros (16)
      {
        name: "Kylian Mbappé",
        position: "Forward",
        overall: 91,
        precio: 7553000,
      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 90,
        precio: 7470000,
      },
      {
        name: "Lautaro Martínez",
        position: "Forward",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Victor Osimhen",
        position: "Forward",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Heung-min Son",
        position: "Forward",
        overall: 87,
        precio: 7221000,
      },
      {name: "Leroy Sané",
        position: "Forward",
        overall: 85,
        precio: 7055000},
      {
        name: "Randal Kolo Muani",
        position: "Forward",
        overall: 84,
        precio: 6972000,
      },
      {name: "João Félix",
        position: "Forward",
        overall: 83,
        precio: 6889000},
      {name: "Rodrygo",
        position: "Forward",
        overall: 85,
        precio: 7055000},
      {name: "Ansu Fati",
        position: "Forward",
        overall: 78,
        precio: 6474000},
      {
        name: "Marcus Rashford",
        position: "Forward",
        overall: 86,
        precio: 7138000,
      },
      {name: "Neymar Jr",
        position: "Forward",
        overall: 89,
        precio: 7387000},
      {
        name: "Karim Adeyemi",
        position: "Forward",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "Jonathan David",
        position: "Forward",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Ángel Di María",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
    ];

    response.json({data: {players: mercadoPlayers}});
  }
);
