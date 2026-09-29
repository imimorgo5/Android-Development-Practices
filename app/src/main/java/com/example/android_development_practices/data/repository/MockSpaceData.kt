package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.model.dto.Agency
import com.example.android_development_practices.data.model.dto.AgencyMini
import com.example.android_development_practices.data.model.dto.AgencyType
import com.example.android_development_practices.data.model.dto.ApiImage
import com.example.android_development_practices.data.model.dto.Astronaut
import com.example.android_development_practices.data.model.dto.AstronautFlight
import com.example.android_development_practices.data.model.dto.AstronautMini
import com.example.android_development_practices.data.model.dto.AstronautStatus
import com.example.android_development_practices.data.model.dto.AstronautType
import com.example.android_development_practices.data.model.dto.AstronautRole
import com.example.android_development_practices.data.model.dto.SpaceStationStatus
import com.example.android_development_practices.data.model.dto.EventType
import com.example.android_development_practices.data.model.dto.Expedition
import com.example.android_development_practices.data.model.dto.GeopoliticalCountry
import com.example.android_development_practices.data.model.dto.Launch
import com.example.android_development_practices.data.model.dto.LaunchStatus
import com.example.android_development_practices.data.model.dto.LauncherConfig
import com.example.android_development_practices.data.model.dto.LauncherConfigFamily
import com.example.android_development_practices.data.model.dto.Mission
import com.example.android_development_practices.data.model.dto.MissionPatch
import com.example.android_development_practices.data.model.dto.NetPrecision
import com.example.android_development_practices.data.model.dto.Orbit
import com.example.android_development_practices.data.model.dto.Pad
import com.example.android_development_practices.data.model.dto.PadLocation
import com.example.android_development_practices.data.model.dto.ProgramNormal
import com.example.android_development_practices.data.model.dto.Rocket
import com.example.android_development_practices.data.model.dto.SpaceEvent
import com.example.android_development_practices.data.model.dto.SpaceStationNormal
import com.example.android_development_practices.data.model.dto.VidUrl
import com.example.android_development_practices.data.model.dto.SpacewalkNormal

object MockSpaceData {

    // ---------- Агентства ----------
    private val nasa = Agency(
        id = 44,
        name = "National Aeronautics and Space Administration",
        abbrev = "NASA",
        type = AgencyType(id = 0, name = "Government"),
        featured = true,
        country = listOf(GeopoliticalCountry(id = 188, name = "United States", alpha_2_code = "US")),
        description = "The National Aeronautics and Space Administration is an independent agency " +
            "of the executive branch of the United States federal government responsible for the " +
            "civilian space program, aeronautics research and space research.",
        administrator = "Bill Nelson",
        founding_year = 1958,
        info_url = "http://www.nasa.gov",
        wiki_url = "https://en.wikipedia.org/wiki/NASA",
        image = ApiImage(name = "NASA logo", image_url = "https://storage.yandexcloud.net/android-practices/nasa.jpeg"),
        logo = ApiImage(name = "NASA logo", image_url = "https://storage.yandexcloud.net/android-practices/nasa.jpeg"),
        total_launch_count = 1017,
        successful_launches = 973,
        failed_launches = 26,
        pending_launches = 14,
        successful_landings = 2,
        attempted_landings = 4,
    )

    private val roscosmos = Agency(
        id = 63,
        name = "Roscosmos State Space Corporation",
        abbrev = "ROSCOSMOS",
        type = AgencyType(id = 0, name = "Government"),
        featured = true,
        country = listOf(GeopoliticalCountry(id = 189, name = "Russia", alpha_2_code = "RU")),
        description = "Roscosmos State Space Corporation is the state corporation of the Russian " +
            "Federation responsible for space flights, cosmonautics programs and aerospace research.",
        administrator = "Yuri Borisov",
        founding_year = 1992,
        info_url = "https://www.roscosmos.ru/",
        wiki_url = "https://en.wikipedia.org/wiki/Roscosmos",
        image = ApiImage(name = "ROSCOSMOS logo", image_url = "https://storage.yandexcloud.net/android-practices/roscosmos.jpeg"),
        logo = ApiImage(name = "ROSCOSMOS logo", image_url = "https://storage.yandexcloud.net/android-practices/roscosmos.jpeg"),
        total_launch_count = 1031,
        successful_launches = 963,
        failed_launches = 27,
        pending_launches = 18,
    )

    private val esa = Agency(
        id = 27,
        name = "European Space Agency",
        abbrev = "ESA",
        type = AgencyType(id = 0, name = "Government"),
        featured = true,
        country = listOf(GeopoliticalCountry(id = 100, name = "France", alpha_2_code = "FR")),
        description = "The European Space Agency is an intergovernmental organisation of 22 member " +
            "states dedicated to the exploration of space.",
        administrator = "Josef Aschbacher",
        founding_year = 1975,
        info_url = "https://www.esa.int/",
        wiki_url = "https://en.wikipedia.org/wiki/European_Space_Agency",
        image = ApiImage(name = "ESA logo", image_url = "https://storage.yandexcloud.net/android-practices/esa.jpg"),
        logo = ApiImage(name = "ESA logo", image_url = "https://storage.yandexcloud.net/android-practices/esa.jpg"),
        total_launch_count = 128,
        successful_launches = 101,
        failed_launches = 2,
        pending_launches = 6,
    )

    private val spacex = Agency(
        id = 121,
        name = "Space Exploration Technologies Corporation",
        abbrev = "SpaceX",
        type = AgencyType(id = 3, name = "Commercial"),
        featured = true,
        country = listOf(GeopoliticalCountry(id = 188, name = "United States", alpha_2_code = "US")),
        description = "Space Exploration Technologies Corporation (SpaceX) is an American aerospace " +
            "manufacturer and space transport services company headquartered in Hawthorne, California.",
        administrator = "Elon Musk",
        founding_year = 2002,
        info_url = "https://www.spacex.com/",
        wiki_url = "https://en.wikipedia.org/wiki/SpaceX",
        image = ApiImage(name = "SpaceX logo", image_url = "https://storage.yandexcloud.net/android-practices/spacex.jpeg"),
        logo = ApiImage(name = "SpaceX logo", image_url = "https://storage.yandexcloud.net/android-practices/spacex.jpeg"),
        total_launch_count = 401,
        successful_launches = 390,
        failed_launches = 6,
        pending_launches = 47,
        successful_landings = 292,
        attempted_landings = 298,
    )

    val agencies: List<Agency> = listOf(nasa, roscosmos, esa, spacex)

    // ---------- Запуски ----------
    private fun statusSuccess() = LaunchStatus(id = 3, name = "Success", abbrev = "Success")
    private fun statusGo() = LaunchStatus(id = 2, name = "Go for Launch", abbrev = "Go")

    val launches: List<Launch> = listOf(
        Launch(
            id = "fc626c08-35b6-4fb0-a1d6-8a1aa1ca98c7",
            name = "Falcon 9 Block 5 | Starlink Group 12-4",
            slug = "falcon-9-block-5-starlink-group-12-4",
            status = statusGo(),
            net = "2026-09-25T01:12:00Z",
            window_start = "2026-09-25T01:12:00Z",
            window_end = "2026-09-25T03:30:00Z",
            net_precision = NetPrecision(id = 4, name = "Minute", abbrev = "Minute"),
            probability = 95,
            weather_concerns = "Low-level wind shear",
            hashtag = "#Starlink",
            webcast_live = false,
            launch_service_provider = AgencyMini(id = 121, name = "SpaceX", abbrev = "SpaceX"),
            rocket = Rocket(id = 2098, configuration = LauncherConfig(id = 209, name = "Falcon 9", families = listOf(LauncherConfigFamily(id = 121, name = "Falcon")), full_name = "Falcon 9 Block 5", variant = "Block 5")),
            mission = Mission(id = 6845, name = "Starlink Group 12-4", description = "A batch of 21 Starlink V2 Mini satellites.",
                orbit = Orbit(id = 7, name = "Low Earth Orbit", abbrev = "LEO")),
            pad = Pad(id = 80, name = "Space Launch Complex 4E", latitude = 34.632,
                longitude = -120.611,
                location = PadLocation(id = 14, name = "Vandenberg SFB, CA, USA"),
                total_launch_count = 189),
            program = listOf(ProgramNormal(id = 8, name = "Starlink", description = "Starlink constellation deployment program.")),
            image = ApiImage(id = 1, name = "Falcon 9 launch", image_url = "https://storage.yandexcloud.net/android-practices/falcon_9.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/falcon_9.jpg", credit = "SpaceX"),
            orbital_launch_attempt_count = 320,
            pad_launch_attempt_count = 100,
            agency_launch_attempt_count = 410,
        ),
        Launch(
            id = "c1fcf2ac-b9bd-4963-bd74-2ff83041a1a8",
            name = "Soyuz 2.1a | Progress MS-32",
            slug = "soyuz-2-1a-progress-ms-32",
            status = LaunchStatus(id = 1, name = "Go for Launch", abbrev = "Go"),
            net = "2026-09-28T07:20:00Z",
            window_start = "2026-09-28T07:20:00Z",
            window_end = "2026-09-28T09:20:00Z",
            net_precision = NetPrecision(id = 4, name = "Minute", abbrev = "Minute"),
            probability = 90,
            webcast_live = false,
            launch_service_provider = AgencyMini(id = 63, name = "ROSCOSMOS", abbrev = "ROSCOSMOS"),
            rocket = Rocket(id = 1980, configuration = LauncherConfig(id = 64, name = "Soyuz", families = listOf(LauncherConfigFamily(id = 999, name = "Soyuz")), full_name = "Soyuz 2.1a", variant = "2.1a")),
            mission = Mission(id = 6733, name = "Progress MS-32", description = "ISS cargo resupply mission with the Progress spacecraft.",
                orbit = Orbit(id = 8, name = "Low Earth Orbit", abbrev = "LEO")),
            orbital_launch_attempt_count = 113,
            pad = Pad(id = 31, name = "1/5", latitude = 45.9203, longitude = 63.3422,
                location = PadLocation(id = 122, name = "Baikonur Cosmodrome, Republic of Kazakhstan"),
                total_launch_count = 313),
            program = listOf(ProgramNormal(id = 20, name = "ISS", description = "International Space Station program.")),
            image = ApiImage(id = 2, name = "Soyuz launch", image_url = "https://storage.yandexcloud.net/android-practices/soyuz_21a.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/soyuz_21a.jpg", credit = "ROSCOSMOS"),
        ),
        Launch(
            id = "9c1d1c9f-8ae1-4f02-b7c2-2c1a57f5ab1f",
            name = "Ariane 6 | ESA Demo Flight 2",
            slug = "ariane-6-esa-demo-flight-2",
            status = statusGo(),
            net = "2026-10-03T13:00:00Z",
            window_start = "2026-10-03T13:00:00Z",
            window_end = "2026-10-03T15:00:00Z",
            net_precision = NetPrecision(id = 3, name = "Day", abbrev = "Day"),
            probability = 80,
            webcast_live = false,
            launch_service_provider = AgencyMini(id = 27, name = "ESA", abbrev = "ESA"),
            rocket = Rocket(id = 1242, configuration = LauncherConfig(id = 905, name = "Ariane 6", families = listOf(LauncherConfigFamily(id = 4, name = "Ariane")), full_name = "Ariane 6", variant = "62")),
            mission = Mission(id = 6901, name = "ESA Demo Flight 2", description = "Second demo flight of the Ariane 6 launcher.",
                orbit = Orbit(id = 11, name = "Geostationary Transfer Orbit", abbrev = "GTO")),
            orbital_launch_attempt_count = 2,
            pad = Pad(id = 44, name = "Ariane Launch Area 4", latitude = 5.236, longitude = -52.768,
                location = PadLocation(id = 96, name = "Guiana Space Centre, French Guiana"),
                total_launch_count = 2),
            program = listOf(ProgramNormal(id = 5, name = "Ariane 6", description = "European launch vehicle program.")),
            image = ApiImage(id = 3, name = "Ariane 6 launch", image_url = "https://storage.yandexcloud.net/android-practices/ariane_6.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/ariane_6.jpg", credit = "ESA/CNES/Arianespace"),
        ),
    )

    // ---------- События ----------
    val events: List<SpaceEvent> = listOf(
        SpaceEvent(
            id = 9210,
            name = "SpaceX commercial crew launch to the ISS",
            date = "2026-10-12T16:00:00Z",
            type = EventType(id = 9, name = "ISS Crew Expedition"),
            description = "A SpaceX Crew Dragon capsule carrying four astronauts to the " +
                "International Space Station as part of the station's long-duration expedition crew.",
            location = "Kennedy Space Center, FL, USA",
            duration = "P1DT",
            webcast_live = false,
            image = ApiImage(id = 4, name = "Crew Dragon", image_url = "https://storage.yandexcloud.net/android-practices/spacex_iss.jpeg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/spacex_iss.jpeg"),
            program = listOf(ProgramNormal(id = 20, name = "ISS", description = "International Space Station program.")),
            vid_urls = listOf(
                VidUrl(
                    priority = 1,
                    source = "YouTube",
                    title = "Crew launch broadcast",
                    url = "https://www.youtube.com/results?search_query=ISS+crew+launch+SpaceX",
                    live = false,
                ),
            ),
        ),
        SpaceEvent(
            id = 9211,
            name = "ISS Expedition 66: Soyuz MS-27 landing",
            date = "2026-10-19T03:45:00Z",
            type = EventType(id = 4, name = "Crew Recovery"),
            description = "The Soyuz MS-27 spacecraft returns to Earth with the crew of the " +
                "66th long-duration expedition to the ISS, landing on the steppe of Kazakhstan.",
            location = "Kazakhstan, Zhezqazghan area",
            duration = "PT45M",
            webcast_live = true,
            image = ApiImage(id = 8, name = "Soyuz MS-27 landing",
                image_url = "https://storage.yandexcloud.net/android-practices/iss_66.jpg"),
            vid_urls = listOf(
                VidUrl(
                    priority = 1,
                    source = "YouTube",
                    title = "Soyuz MS-27 landing broadcast",
                    url = "https://www.youtube.com/results?search_query=Soyuz+MS-27+landing",
                    live = true,
                ),
            ),
        ),
        SpaceEvent(
            id = 9212,
            name = "Intuitive Machines IM-3 lunar landing attempt",
            date = "2026-10-25T21:00:00Z",
            type = EventType(id = 7, name = "Lunar Landing"),
            description = "Attempted landing of the Intuitive Machines Nova-C lunar lander, " +
                "carrying NASA CLPS payloads to the lunar south pole region.",
            location = "Mons Mouton, lunar south pole",
            duration = "PT15M",
            webcast_live = false,
            image = ApiImage(id = 5, name = "Nova-C", image_url = "https://storage.yandexcloud.net/android-practices/im3_lunar.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/im3_lunar.jpg"),
        ),
    )

    // ---------- Экспедиции ----------
    val expeditions: List<Expedition> = listOf(
        Expedition(
            id = 70,
            name = "Expedition 70",
            start = "2023-09-27T00:00:00Z",
            end = "2024-03-28T00:00:00Z",
            spacestation = SpaceStationNormal(id = 4, name = "International Space Station",
                status = SpaceStationStatus(id = 1, name = "Active"), founded = "1998-11-20",
                description = "The International Space Station is a large spacecraft in orbit around " +
                    "Earth that serves as a home where crews of astronauts and cosmonauts live."),
            mission_patches = listOf(MissionPatch(id = 1, name = "ISS Expedition 70 Patch",
                image_url = "https://storage.yandexcloud.net/android-practices/exp_70.jpeg", priority = 100)),
            crew = listOf(
                AstronautFlight(id = 1, role = AstronautRole(id = 1, role = "Commander"),
                    astronaut = AstronautMini(id = 318, name = "Andreas Mogensen", nationality = listOf(GeopoliticalCountry(id = 0, name = "Denmark", alpha_2_code = "DK")))),
                AstronautFlight(id = 2, role = AstronautRole(id = 2, role = "Flight Engineer"),
                    astronaut = AstronautMini(id = 277, name = "Konstantin Borisov", nationality = listOf(GeopoliticalCountry(id = 0, name = "Russia", alpha_2_code = "RU")))),
            ),
            spacewalks = listOf(SpacewalkNormal(id = 1, name = "EVA 1", start = "2023-10-20", end = "2023-10-20", duration = "P1DT")),
        ),
        Expedition(
            id = 71,
            name = "Expedition 71",
            start = "2024-03-28T00:00:00Z",
            end = "2025-02-15T00:00:00Z",
            spacestation = SpaceStationNormal(id = 4, name = "International Space Station",
                status = SpaceStationStatus(id = 1, name = "Active"), founded = "1998-11-20",
                description = "The International Space Station is a large spacecraft in orbit around Earth."),
            mission_patches = listOf(MissionPatch(id = 2, name = "ISS Expedition 71 Patch",
                image_url = "https://storage.yandexcloud.net/android-practices/exp_71.jpeg", priority = 100)),
            crew = listOf(
                AstronautFlight(id = 3, role = AstronautRole(id = 2, role = "Flight Engineer"),
                    astronaut = AstronautMini(id = 700, name = "Tracy Caldwell Dyson", nationality = listOf(GeopoliticalCountry(id = 0, name = "United States", alpha_2_code = "US")))),
                AstronautFlight(id = 4, role = AstronautRole(id = 1, role = "Commander"),
                    astronaut = AstronautMini(id = 553, name = "Oleg Kononenko", nationality = listOf(GeopoliticalCountry(id = 0, name = "Russia", alpha_2_code = "RU")))),
            ),
        ),
        Expedition(
            id = 72,
            name = "Expedition 72",
            start = "2025-02-15T00:00:00Z",
            end = null,
            spacestation = SpaceStationNormal(id = 4, name = "International Space Station",
                status = SpaceStationStatus(id = 1, name = "Active"), founded = "1998-11-20"),
            mission_patches = listOf(MissionPatch(id = 3, name = "ISS Expedition 72 Patch",
                image_url = "https://storage.yandexcloud.net/android-practices/exp_72.jpg", priority = 100)),
            crew = listOf(
                AstronautFlight(id = 5, role = AstronautRole(id = 1, role = "Commander"),
                    astronaut = AstronautMini(id = 660, name = "Sunita Williams", nationality = listOf(GeopoliticalCountry(id = 0, name = "United States", alpha_2_code = "US")))),
            ),
        ),
    )

    // ---------- Астронавты ----------
    private fun country(code: String, name: String) = GeopoliticalCountry(id = 0, name = name, alpha_2_code = code)

    val astronauts: List<Astronaut> = listOf(
        Astronaut(
            id = 553,
            name = "Oleg Kononenko",
            status = AstronautStatus(id = 1, name = "Active"),
            type = AstronautType(id = 0, name = "Government"),
            agency = AgencyMini(id = 63, name = "ROSCOSMOS", abbrev = "ROSCOSMOS"),
            in_space = false,
            time_in_space = "PT1111D6H",
            eva_time = "PT57H22M",
            date_of_birth = "1964-06-21",
            age = 62,
            nationality = listOf(country("RUS", "Russia")),
            bio = "Oleg Dmitriyevich Kononenko is a Russian cosmonaut who has made four long-duration " +
                "stays on the International Space Station, holding records for cumulative time in space.",
            wiki = "https://en.wikipedia.org/wiki/Oleg_Kononenko",
            first_flight = "2008-04-08",
            last_flight = "2020-04-17",
            flights_count = 4,
            spacewalks_count = 7,
            image = ApiImage(id = 6, name = "Oleg Kononenko", image_url = "https://storage.yandexcloud.net/android-practices/oleg_kononenko.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/oleg_kononenko.jpg"),
        ),
        Astronaut(
            id = 660,
            name = "Sunita Williams",
            status = AstronautStatus(id = 1, name = "Active"),
            type = AstronautType(id = 0, name = "Government"),
            agency = AgencyMini(id = 44, name = "NASA", abbrev = "NASA"),
            in_space = true,
            time_in_space = "PT1063D1H",
            eva_time = "PT74H2M",
            date_of_birth = "1965-09-19",
            age = 61,
            nationality = listOf(country("USA", "United States")),
            bio = "Sunita Lyn Williams is an American astronaut and United States Navy officer. " +
                "She holds the record for total time spent on spacewalks by a woman.",
            wiki = "https://en.wikipedia.org/wiki/Sunita_Williams",
            first_flight = "2006-12-09",
            last_flight = "2024-06-05",
            flights_count = 3,
            landings_count = 3,
            spacewalks_count = 10,
            image = ApiImage(id = 7, name = "Sunita Williams", image_url = "https://storage.yandexcloud.net/android-practices/sunita_williams.jpg",
                thumbnail_url = "https://storage.yandexcloud.net/android-practices/sunita_williams.jpg"),
        ),
        Astronaut(
            id = 700,
            name = "Tracy Caldwell Dyson",
            status = AstronautStatus(id = 1, name = "Active"),
            type = AstronautType(id = 0, name = "Government"),
            agency = AgencyMini(id = 44, name = "NASA", abbrev = "NASA"),
            in_space = true,
            time_in_space = "PT370D",
            date_of_birth = "1969-08-14",
            age = 57,
            nationality = listOf(country("USA", "United States")),
            bio = "Tracy Caldwell Dyson is an American chemist and NASA astronaut.",
            wiki = "https://en.wikipedia.org/wiki/Tracy_Caldwell_Dyson",
            first_flight = "2007-08-08",
            flights_count = 3,
            spacewalks_count = 4,
            image = ApiImage(id = 9, name = "Tracy Caldwell Dyson",
                image_url = "https://storage.yandexcloud.net/android-practices/tracy_dyson.jpg"),
        ),
        Astronaut(
            id = 318,
            name = "Andreas Mogensen",
            status = AstronautStatus(id = 3, name = "Retired"),
            type = AstronautType(id = 0, name = "Government"),
            agency = AgencyMini(id = 27, name = "ESA", abbrev = "ESA"),
            in_space = false,
            time_in_space = "PT209D1H",
            date_of_birth = "1976-11-02",
            age = 49,
            nationality = listOf(country("DNK", "Denmark")),
            bio = "Andreas Enevold Mogensen is a Danish engineer and ESA astronaut, the first Dane in space.",
            wiki = "https://en.wikipedia.org/wiki/Andreas_Mogensen",
            first_flight = "2015-09-02",
            last_flight = "2024-02-29",
            flights_count = 2,
            spacewalks_count = 1,
            image = ApiImage(id = 10, name = "Andreas Mogensen",
                image_url = "https://storage.yandexcloud.net/android-practices/andreas_mogensen.jpeg"),
        ),
    )
}