import * as functions from "firebase-functions";

export const getLiverpoolPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Liverpool/";
    const liverpoolPlayers = [
      {
        name: "Alisson Becker",
        position: "Portero",
        overall: 89,
        precio: 22000000,
        url: `${storageBaseUrl}Alisson.png`,
      },
      {
        name: "Caoimhín Kelleher",
        position: "Portero",
        overall: 78,
        precio: 5000000,
        url: `${storageBaseUrl}Kelleher.png`,
      },
      {
        name: "Virgil van Dijk",
        position: "Defensa",
        overall: 89,
        precio: 22000000,
        url: `${storageBaseUrl}Virgil.png`,
      },
      {
        name: "Ibrahima Konaté",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Konaté.png`,
      },
      {
        name: "Joe Gomez",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Gomez.png`,
      },
      {
        name: "Andy Robertson",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Robertson.png`,
      },
      {
        name: "Trent Alexander-Arnold",
        position: "Defensa",
        overall: 87,
        precio: 15000000,
        url: `${storageBaseUrl}Alexander.png`,
      },
      {
        name: "Kostas Tsimikas",
        position: "Defensa",
        overall: 77,
        precio: 4000000,
        url: `${storageBaseUrl}Tsimikas.png`,
      },
      {
        name: "Jarell Quansah",
        position: "Defensa",
        overall: 70,
        precio: 2000000,
        url: `${storageBaseUrl}Quansah.png`,
      },
      {
        name: "Conor Bradley",
        position: "Defensa",
        overall: 72,
        precio: 3000000,
        url: `${storageBaseUrl}Bradley.png`,
      },
      {
        name: "Wataru Endo",
        position: "Mediocentro",
        overall: 81,
        precio: 8000000,
        url: `${storageBaseUrl}Wataru.png`,
      },
      {
        name: "Alexis Mac Allister",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Allister.png`,
      },
      {
        name: "Dominik Szoboszlai",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Szoboszlai.png`,
      },
      {
        name: "Curtis Jones",
        position: "Mediocentro",
        overall: 78,
        precio: 5000000,
        url: `${storageBaseUrl}Curtis.png`,
      },
      {
        name: "Harvey Elliott",
        position: "Mediocentro",
        overall: 77,
        precio: 4000000,
        url: `${storageBaseUrl}Elliott.png`,
      },
      {
        name: "Ryan Gravenberch",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Gravenberch.png`,
      },
      {
        name: "Tyler Morton",
        position: "Mediocentro",
        overall: 72,
        precio: 2500000,
        url: `${storageBaseUrl}Morton.png`,
      },
      {
        name: "Mohamed Salah",
        position: "Delantero",
        overall: 90,
        precio: 25000000,
        url: `${storageBaseUrl}Mohamed.png`,
      },
      {
        name: "Luis Díaz",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Luis.png`,
      },
      {
        name: "Darwin Núñez",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Darwin.png`,
      },
      {
        name: "Diogo Jota",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Diogo.png`,
      },
      {
        name: "Cody Gakpo",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Gakpo.png`,
      },
      {
        name: "Federico Chiesa",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Chiesa.png`,
      },
    ];
    const totalOverall = liverpoolPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / liverpoolPlayers.length;

    response.json({data: {players: liverpoolPlayers, averageOverall}});
  }
);
