import * as functions from "firebase-functions";

export const getAtleticoPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/AtleMadrid/";
    const atleticoPlayers = [
      {
        name: "Jan Oblak",
        position: "Goalkeeper",
        overall: 88,
        precio: 7744000,
        url: `${storageBaseUrl}OBLAK.png`,
      },
      {
        name: "J. Musso",
        position: "Goalkeeper",
        overall: 77,
        precio: 5929000,
        url: `${storageBaseUrl}Musso.png`,
      },
      {
        name: "José María Giménez",
        position: "Defender",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}JMGimenez.png`,
      },
      {
        name: "Azpilicueta",
        position: "Defender",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Azpilicueta.webp`,
      },
      {
        name: "Lenglet",
        position: "Defender",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Lenglet.png`,
      },
      {
        name: "Nahuel Molina",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Molina.png`,
      },
      {
        name: "Axel Witsel",
        position: "Defender",
        overall: 79,
        precio: 6241000,
        url: `${storageBaseUrl}Witsel.png`,
      },
      {
        name: "Javi Galán",
        position: "Defender",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}JaviGalan.png`,
      },
      {
        name: "Reinildo",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Reinildo.png`,
      },
      {
        name: "Le Normand",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}LeNormand.png`,
      },
      {
        name: "Gallagher",
        position: "Midfielder",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Gallagher.png`,
      },
      {
        name: "Rodrigo De Paul",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}DePaul.png`,
      },
      {
        name: "Koke",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Koke.png`,
      },
      {
        name: "Barrios",
        position: "Midfielder",
        overall: 88,
        precio: 7744000,
        url: `${storageBaseUrl}Barrios.png`,
      },
      {
        name: "Lemar",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Lemar.png`,
      },
      {
        name: "Samuel Lino",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Lino.png`,
      },
      {
        name: "Marcos Llorente",
        position: "Midfielder",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Llorente.webp`,
      },
      {
        name: "Riquelme",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Riquelme.png`,
      },
      {
        name: "Griezmann",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Griezmann.png`,
      },
      {
        name: "Sorloth",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Sorloth.png`,
      },
      {
        name: "Correa",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Correa.png`,
      },
      {
        name: "Julian Alvarez",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}JAlvarez.png`,
      },
      {
        name: "Giuliano",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}giuliano.webp`,
      },
    ];

    const totalOverall = atleticoPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / atleticoPlayers.length;

    response.json({data: {players: atleticoPlayers, averageOverall}});
  }
);
