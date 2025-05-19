import * as functions from "firebase-functions";

export const getBarcelonaPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/FcBarcelona/";

    const barcelonaPlayers = [
      {
        name: "Marc-André ter Stegen",
        position: "Goalkeeper",
        overall: 89,
        precio: 7921000,
        url: `${storageBaseUrl}01-Ter_Stegen-M.webp`,
      },
      {
        name: "Iñaki Peña",
        position: "Goalkeeper",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}13-Inaki_Pena-M.webp`,
      },
      {
        name: "Wojciech Szczęsny",
        position: "Goalkeeper",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}25-Szczesny-M.webp`,
      },
      {
        name: "Pau Cubarsí",
        position: "Defender",
        overall: 78,
        precio: 6084000,
        url: `${storageBaseUrl}02-Cubarsi-M.webp`,
      },
      {
        name: "Alejandro Balde",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}03-Balde-M.webp`,
      },
      {
        name: "Ronald Araujo",
        position: "Defender",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}04-Araujo-M.webp`,
      },
      {
        name: "Iñigo Martínez",
        position: "Defender",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}05-Martinez-M.webp`,
      },
      {
        name: "Andreas Christensen",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}15-Christensen-M.webp`,
      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}23-Kounde-M.webp`,
      },
      {
        name: "Eric García",
        position: "Defender",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}24-Eric_Garcia-M.webp`,
      },
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 87,
        precio: 7569000,
        url: `${storageBaseUrl}21-De_Jong-M.webp`,
      },
      {
        name: "Pedri",
        position: "Midfielder",
        overall: 88,
        precio: 7744000,
        url: `${storageBaseUrl}08-Pedri-M.webp`,
      },
      {
        name: "Gavi",
        position: "Midfielder",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}06-Gavi-M.webp`,
      },
      {
        name: "Casadó",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}17-Casado-M.webp`,
      },
      {
        name: "Torre",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}14-Pablo_Torre-M.webp`,
      },
      {
        name: "Ansu Fati",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}10-Ansu_Fati-M.webp`,
      },
      {
        name: "Ferran Torres",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}07-Ferran_Torres-M.webp`,
      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 91,
        precio: 8281000,
        url: `${storageBaseUrl}09-Lewandowski-M.webp`,
      },
      {
        name: "Olmo",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}20-Olmo-M.webp`,
      },
      {
        name: "Pau Victor",
        position: "Forward",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}18-Pau_Victor-M.webp`,
      },
      {
        name: "Raphinha",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}11-Raphinha-M.webp`,
      },
      {
        name: "Yamal",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}19-Lamine-M.webp`,
      },
    ];
    const totalOverall = barcelonaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / barcelonaPlayers.length;

    response.json({data: {players: barcelonaPlayers, averageOverall}});
  }
);
