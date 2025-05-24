import * as functions from "firebase-functions";

export const getAtleticoPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/AtleMadrid/";
    const atleticoPlayers = [
      {
        name: "Jan Oblak",
        position: "Portero",
        overall: 88,
        precio: 25000000,
        url: `${storageBaseUrl}OBLAK.png`,
      },
      {
        name: "J. Musso",
        position: "Portero",
        overall: 77,
        precio: 3000000,
        url: `${storageBaseUrl}Musso.png`,
      },
      {
        name: "José María Giménez",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}JMGimenez.png`,
      },
      {
        name: "Azpilicueta",
        position: "Defensa",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Azpilicueta.webp`,
      },
      {
        name: "Lenglet",
        position: "Defensa",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Lenglet.png`,
      },
      {
        name: "Nahuel Molina",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Molina.png`,
      },
      {
        name: "Axel Witsel",
        position: "Defensa",
        overall: 79,
        precio: 4000000,
        url: `${storageBaseUrl}Witsel.png`,
      },
      {
        name: "Javi Galán",
        position: "Defensa",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}JaviGalan.png`,
      },
      {
        name: "Reinildo",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Reinildo.png`,
      },
      {
        name: "Le Normand",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}LeNormand.png`,
      },
      {
        name: "Gallagher",
        position: "Mediocentro",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Gallagher.png`,
      },
      {
        name: "Rodrigo De Paul",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}DePaul.png`,
      },
      {
        name: "Koke",
        position: "Mediocentro",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Koke.png`,
      },
      {
        name: "Barrios",
        position: "Mediocentro",
        overall: 88,
        precio: 25000000,
        url: `${storageBaseUrl}Barrios.png`,
      },
      {
        name: "Lemar",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Lemar.png`,
      },
      {
        name: "Samuel Lino",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Lino.png`,
      },
      {
        name: "Marcos Llorente",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Llorente.webp`,
      },
      {
        name: "Riquelme",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Riquelme.png`,
      },
      {
        name: "Griezmann",
        position: "Delantero",
        overall: 85,
        precio: 18000000,
        url: `${storageBaseUrl}Griezmann.png`,
      },
      {
        name: "Sorloth",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Sorloth.png`,
      },
      {
        name: "Correa",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Correa.png`,
      },
      {
        name: "Julian Alvarez",
        position: "Delantero",
        overall: 85,
        precio: 18000000,
        url: `${storageBaseUrl}JAlvarez.png`,
      },
      {
        name: "Giuliano",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}giuliano.webp`,
      },
    ];

    const totalOverall = atleticoPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / atleticoPlayers.length;

    response.json({data: {players: atleticoPlayers, averageOverall}});
  }
);
