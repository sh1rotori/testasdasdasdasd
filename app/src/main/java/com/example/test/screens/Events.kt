package com.example.test.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.test.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class EventViewModel : ViewModel() {
    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent

    fun setSelectedEvent(event: Event) {
        _selectedEvent.value = event
    }
    fun updateReadStatus(eventId: String) {
        _selectedEvent.value?.let { event ->
            if (event.eventId == eventId) {
                event.eventReadStatus = true
                _selectedEvent.value = event.copy() // Обновляем объект, чтобы уведомить Flow
            }
        }
    }

}

@Composable
fun EventsScreen(eventViewModel: EventViewModel, onEventClick: (Event) -> Unit) {

    val eventViewModel: EventViewModel = viewModel()

    // Placeholder for the event list
    val eventsList = mutableListOf<Event>()

    // Placeholder for the selected filter
    var selectedFilter by remember { mutableStateOf(EventFilter.All) }

    // Read events data from JSON file
    val eventsData: List<Event> = readEventsData()

    // Apply the selected filter to the events list
    val filteredEvents = when (selectedFilter) {
        EventFilter.All -> eventsData
        EventFilter.Unread -> eventsData.filter { !it.eventReadStatus }
        EventFilter.Read -> eventsData.filter { it.eventReadStatus }
    }


    // UI for the Events Screen
    // UI for the Events Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            // Title
            Text(
                text = "События",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            // Spacer
            Spacer(modifier = Modifier.height(16.dp))

            // Filter Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 16.dp), // Adjust bottom padding
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                FilterButton("Все", selectedFilter == EventFilter.All) {
                    selectedFilter = EventFilter.All
                }
                FilterButton("Непрочитанное", selectedFilter == EventFilter.Unread) {
                    selectedFilter = EventFilter.Unread
                }
                FilterButton("Прочитанное", selectedFilter == EventFilter.Read) {
                    selectedFilter = EventFilter.Read
                }
            }

            // Event List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(filteredEvents) { event ->
                    EventListItem(event) {
                        // Обработка события прочтения
                        event?.let {
                            if (!it.eventReadStatus) {
                                eventViewModel.updateReadStatus(it.eventId)
                            }
                        }
                        eventViewModel.setSelectedEvent(event)
                        onEventClick(event)
                        // Handle item click
                    }
                }
            }
        }

    }
}

@Composable
fun EventListItem(event: Event, onClick: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                onClick()
            }
            .padding(8.dp)
    ) {
        // Display event details
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/images/${event.eventPictures.firstOrNull()}"),
            contentDescription = "Event Image",
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
        )
        Text(
            text = event.eventTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = event.eventText,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = if (event.eventReadStatus) "Прочитано" else "Непрочитано",
            color = if (event.eventReadStatus) Color.Gray else Color.Green,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    // UI for filter buttons
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (isSelected) Color.Gray else Color.Transparent
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

enum class EventFilter {
    All,
    Unread,
    Read
}
@Serializable
data class Event(
    val eventId: String,
    val eventTitle: String,
    val eventText: String,
    var eventReadStatus: Boolean,
    val eventPictures: List<String>
)

// Function to read events data from JSON
fun readEventsData(): List<Event> {
    // Parse JSON data here
    val jsonString = """
        [
    {
        "eventId": "EVENT_0000",
        "eventTitle": "WorldSkills Competition 2022 Special Edition",
        "eventText": "This year, 61 international skill competitions will take place across Europe, North America, and East Asia from September to November 2022.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_00_A.jpg",
            "events_00_B.jpg",
            "events_00_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0001",
        "eventTitle": "A unique format for international skill competitions",
        "eventText": "WorldSkills is preparing a unique format for international skill competitions in 2022, showcasing 61 skills in 15 different countries and regions around the world. WorldSkills Competition 2022 Special Edition (WSC2022SE) is the official replacement for WorldSkills Shanghai 2022, cancelled in May due to the pandemic.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_01_A.jpg",
            "events_01_B.jpg",
            "events_01_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0002",
        "eventTitle": "Thanks to partners",
        "eventText": "Thanks to the commitment of Partners and 15 Member countries and regions organizing the individual skill competitions, the dates and cities have been set. The 61 skill competitions will be held over 12 weeks, starting 7 September and ending 26 November 2022.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_02_A.jpg",
            "events_02_B.jpg",
            "events_02_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0003",
        "eventTitle": "The countries and regions hosting skill competitions",
        "eventText": "The countries and regions hosting skill competitions for WorldSkills Competition 2022 Special Edition are:\n·Austria\n·Canada\n·Denmark\n·Estonia\n·Finland\n·France\n·Germany\n·South Tyrol, Italy\n·Japan\n·Korea\n·Luxembourg\n·Sweden\n·Switzerland\n·United Kingdom\n·United States of America",
        "eventReadStatus": false,
        "eventPictures": [
            "events_03_A.jpg",
            "events_03_B.jpg",
            "events_03_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0004",
        "eventTitle": "A unforgettable Competition",
        "eventText": "Over 1,000 Competitors from 58 countries and regions will participate in WorldSkills Competition 2022 Special Edition. These events reestablish the biennial cycle of WorldSkills Competitions, disrupted due to the pandemic. Ongoing monitoring of local and global pandemic controls will be incorporated into health and safety protocols for each skill competition.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_04_A.jpg",
            "events_04_B.jpg",
            "events_04_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0005",
        "eventTitle": "Competition will be held in Bordeaux, France",
        "eventText": "Competition will be held in Bordeaux, France on 19—22 October 2022. It will hold Skills: Digital Construction, Health and Social Care, Mechanical Engineering CAD, Mobile Robotics, Plastering and Drywall Systems",
        "eventReadStatus": false,
        "eventPictures": [
            "events_05_A.jpg",
            "events_05_B.jpg",
            "events_05_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0006",
        "eventTitle": "Competition will be held in Goyang, Korea",
        "eventText": "Competition will be held in Goyang, Korea on 13—16 October 2022. It will hold Skills: 3D Digital Game Art, Cloud Computing, Cyber Security, IT Network Systems Administration, IT Software Solutions for Business, Mobile Applications Development, Plastic Die Engineering, Web Technologies.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_06_A.jpg",
            "events_06_B.jpg",
            "events_06_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0007",
        "eventTitle": "WorldSkills history",
        "eventText": "The Competition moves from Kazan, Russia and then to Shanghai, China, but the WorldSkills movement has become much more than an international competition. The organisation is helping young people around the world change their lives through vocational skills.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_07_A.jpg",
            "events_07_B.jpg",
            "events_07_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0008",
        "eventTitle": "Competition will be held in Salzburg, Austria",
        "eventText": "Competition will be held in Salzburg, Austria on 24—26 November 2022. It will hold Skills:  Bricklaying, Chemical Laboratory Technology, Concrete Construction Work, Electrical Installations, Freight Forwarding, Heavy Vehicle Technology, Industrial Control.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_08_A.jpg",
            "events_08_B.jpg",
            "events_08_C.jpg"
        ]
    },
    {
        "eventId": "EVENT_0009",
        "eventTitle": "Sponsorship and Partnership",
        "eventText": "Under the framework of WorldSkills Sponsorship and Partnership, we will develop a comprehensive sponsorship strategy.",
        "eventReadStatus": false,
        "eventPictures": [
            "events_09_A.jpg",
            "events_09_B.jpg",
            "events_09_C.jpg"
        ]
    }
]
    """.trimIndent()

    return Json.decodeFromString<List<Event>>(jsonString)
}
