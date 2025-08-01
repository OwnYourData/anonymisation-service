package eu.ownyourdata.anonymisationservice.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RequestDTO(

    @field:Schema(description = "The configuration URL", example = "https://soya.ownyourdata.eu/AnonymisationDemo")
    val configurationURL: String,

    @field:Schema(
        description = "Data to be anonymized",
        example = """
            [
                {
                    "Name": "Linda Schäfer",
                    "Adresse": {
                        "Detail": "Martin-Fleck-Straße 0/1",
                        "Zip": "9788",
                        "State": "Wien",
                        "City": "Hainfeld",
                        "Country": "Austria"
                    },
                    "Latitude": 26.042505,
                    "Longitude": -21.316927,
                    "Geburtsdatum": "1956-09-17",
                    "Gewicht": 76.58910075827146,
                    "Körpergröße": 161.43868377230694
                },
                {
                    "Name": "Konstantin Kargl",
                    "Adresse": {
                        "Detail": "Sven-Jovanovic-Straße 57",
                        "Zip": "9832",
                        "State": "Niederösterreich",
                        "City": "Eggenburg",
                        "Country": "Austria"
                    },
                    "Latitude": -40.1111005,
                    "Longitude": -89.225656,
                    "Geburtsdatum": "1943-09-27",
                    "Gewicht": 59.946366674906116,
                    "Körpergröße": 157.26227402742805
                },
                {
                    "Name": "Anastasia Nowak",
                    "Adresse": {
                        "Detail": "Chris-Uhl-Gasse 1",
                        "Zip": "3174",
                        "State": "Oberösterreich",
                        "City": "Innsbruck",
                        "Country": "Austria"
                    },
                    "Latitude": 33.132258,
                    "Longitude": -156.279897,
                    "Geburtsdatum": "1979-06-05",
                    "Gewicht": 82.80049737623607,
                    "Körpergröße": 196.4194865819462
                },
                {
                    "Name": "Eva Lintner",
                    "Adresse": {
                        "Detail": "Nadja-Fercher-Ring 9",
                        "Zip": "4394",
                        "State": "Tirol",
                        "City": "Mistelbach an der Zaya",
                        "Country": "Austria"
                    },
                    "Latitude": 72.9796475,
                    "Longitude": -48.067117,
                    "Geburtsdatum": "2015-03-17",
                    "Gewicht": 61.67142751140824,
                    "Körpergröße": 188.49913212310818
                },
                {
                    "Name": "Kalina Vogl",
                    "Adresse": {
                        "Detail": "Vogtstraße 969",
                        "Zip": "6821",
                        "State": "Steiermark",
                        "City": "Marchegg",
                        "Country": "Austria"
                    },
                    "Latitude": -35.5334215,
                    "Longitude": -163.191949,
                    "Geburtsdatum": "1945-05-07",
                    "Gewicht": 68.60963524662719,
                    "Körpergröße": 172.80842630245004
                },
                {
                    "Name": "Friedrich Ritter",
                    "Adresse": {
                        "Detail": "Schenkgasse 526",
                        "Zip": "7721",
                        "State": "Oberösterreich",
                        "City": "Marchegg",
                        "Country": "Austria"
                    },
                    "Latitude": 10.314603,
                    "Longitude": 117.589799,
                    "Geburtsdatum": "1912-10-01",
                    "Gewicht": 99.28533696384346,
                    "Körpergröße": 212.03018252030472
                },
                {
                    "Name": "Carlotta Raab",
                    "Adresse": {
                        "Detail": "Baumannring 3",
                        "Zip": "7354",
                        "State": "Niederösterreich",
                        "City": "Traiskirchen",
                        "Country": "Austria"
                    },
                    "Latitude": 73.354393,
                    "Longitude": -155.14681,
                    "Geburtsdatum": "1934-09-14",
                    "Gewicht": 41.61453154260245,
                    "Körpergröße": 158.5378768511823
                },
                {
                    "Name": "Nikolai Eichberger",
                    "Adresse": {
                        "Detail": "Antonella-Schweighofer-Ring 8",
                        "Zip": "2398",
                        "State": "Oberösterreich",
                        "City": "Waidhofen an der Thaya",
                        "Country": "Austria"
                    },
                    "Latitude": -13.8937915,
                    "Longitude": -144.311332,
                    "Geburtsdatum": "2010-07-28",
                    "Gewicht": 91.50471442902322,
                    "Körpergröße": 196.56708566602626
                },
                {
                    "Name": "Andrea Jost",
                    "Adresse": {
                        "Detail": "Kaltenböckstraße 25",
                        "Zip": "3556",
                        "State": "Salzburg",
                        "City": "Fürstenfeld",
                        "Country": "Austria"
                    },
                    "Latitude": 81.801259,
                    "Longitude": 78.83568,
                    "Geburtsdatum": "1910-03-18",
                    "Gewicht": 78.86084566700029,
                    "Körpergröße": 169.88181531921055
                },
                {
                    "Name": "Constantin Kuhn",
                    "Adresse": {
                        "Detail": "Schlagerring 2/9",
                        "Zip": "2275",
                        "State": "Wien",
                        "City": "Korneuburg",
                        "Country": "Austria"
                    },
                    "Latitude": 69.761871,
                    "Longitude": 52.364912,
                    "Geburtsdatum": "1973-06-27",
                    "Gewicht": 77.6835824383487,
                    "Körpergröße": 183.09521048240862
                },
                {
                    "Name": "Ferdinand Zöchling",
                    "Adresse": {
                        "Detail": "Pöschlstr. 9/9",
                        "Zip": "4488",
                        "State": "Kärnten",
                        "City": "Linz",
                        "Country": "Austria"
                    },
                    "Latitude": 6.6866125,
                    "Longitude": 99.606215,
                    "Geburtsdatum": "1970-06-13",
                    "Gewicht": 80.83584147689601,
                    "Körpergröße": 161.40301603940856
                },
                {
                    "Name": "Luisa Geyer",
                    "Adresse": {
                        "Detail": "Norah-Enzinger-Gasse 9",
                        "Zip": "5631",
                        "State": "Steiermark",
                        "City": "Klosterneuburg",
                        "Country": "Austria"
                    },
                    "Latitude": 31.7739185,
                    "Longitude": 111.180846,
                    "Geburtsdatum": "1930-12-23",
                    "Gewicht": 79.41193280746374,
                    "Körpergröße": 198.45993664531244
                },
                {
                    "Name": "Patrik Schröder",
                    "Adresse": {
                        "Detail": "Linderstr. 3/6",
                        "Zip": "4371",
                        "State": "Burgenland",
                        "City": "Leibnitz",
                        "Country": "Austria"
                    },
                    "Latitude": 40.7436975,
                    "Longitude": -170.190902,
                    "Geburtsdatum": "1941-06-09",
                    "Gewicht": 58.27479457737428,
                    "Körpergröße": 169.58705148833857
                },
                {
                    "Name": "Henry Deutschmann",
                    "Adresse": {
                        "Detail": "Paulitschring 74",
                        "Zip": "4665",
                        "State": "Burgenland",
                        "City": "Ebreichsdorf",
                        "Country": "Austria"
                    },
                    "Latitude": 13.151011,
                    "Longitude": -146.285269,
                    "Geburtsdatum": "1982-01-17",
                    "Gewicht": 49.536959997912454,
                    "Körpergröße": 168.16134459901144
                },
                {
                    "Name": "Hanna Widhalm",
                    "Adresse": {
                        "Detail": "Kreinerplatz 997",
                        "Zip": "1958",
                        "State": "Niederösterreich",
                        "City": "Baden",
                        "Country": "Austria"
                    },
                    "Latitude": 52.6367265,
                    "Longitude": 59.892808,
                    "Geburtsdatum": "2021-08-13",
                    "Gewicht": 86.03033451617878,
                    "Körpergröße": 167.04839652567566
                },
                {
                    "Name": "Ralph Krainer",
                    "Adresse": {
                        "Detail": "Sandra-Köhler-Weg 257",
                        "Zip": "8206",
                        "State": "Salzburg",
                        "City": "Braunau am Inn",
                        "Country": "Austria"
                    },
                    "Latitude": -72.0569925,
                    "Longitude": -169.554987,
                    "Geburtsdatum": "1954-06-30",
                    "Gewicht": 73.5687996346073,
                    "Körpergröße": 182.14751478407905
                },
                {
                    "Name": "Carlo Sattler",
                    "Adresse": {
                        "Detail": "Bachingerplatz 97",
                        "Zip": "1729",
                        "State": "Vorarlberg",
                        "City": "Kindberg",
                        "Country": "Austria"
                    },
                    "Latitude": 11.0239045,
                    "Longitude": 148.137543,
                    "Geburtsdatum": "1959-04-14",
                    "Gewicht": 82.02018210685782,
                    "Körpergröße": 193.42740834425666
                },
                {
                    "Name": "Elena Neureiter",
                    "Adresse": {
                        "Detail": "Florian-Guggenberger-Ring 3",
                        "Zip": "2697",
                        "State": "Tirol",
                        "City": "Litschau",
                        "Country": "Austria"
                    },
                    "Latitude": -88.620158,
                    "Longitude": -79.275536,
                    "Geburtsdatum": "1941-02-17",
                    "Gewicht": 56.042604134807014,
                    "Körpergröße": 171.57481393877615
                },
                {
                    "Name": "Francesca Kaltenbrunner",
                    "Adresse": {
                        "Detail": "Christopher-Stifter-Ring 376",
                        "Zip": "9009",
                        "State": "Salzburg",
                        "City": "Ternitz",
                        "Country": "Austria"
                    },
                    "Latitude": -60.7893145,
                    "Longitude": -55.802177,
                    "Geburtsdatum": "1980-08-29",
                    "Gewicht": 91.17411219640115,
                    "Körpergröße": 159.24047751427167
                },
                {
                    "Name": "Ing. Rene Schrenk",
                    "Adresse": {
                        "Detail": "Kainzring 7/8",
                        "Zip": "4656",
                        "State": "Vorarlberg",
                        "City": "Friedberg",
                        "Country": "Austria"
                    },
                    "Latitude": -83.3957765,
                    "Longitude": -134.251883,
                    "Geburtsdatum": "1981-02-21",
                    "Gewicht": 72.42450701754538,
                    "Körpergröße": 155.38769811838898
                }
            ]
        """
    )
    val data: List<Map<String, Any>>,
)