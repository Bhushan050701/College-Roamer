package com.example.collegeroamer

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

class NavigationDisplayActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var pathImageView: ImageView

    private lateinit var path: List<String>

    private val locationImages = mapOf(
        "Class 601" to R.drawable.sixthfloor_class601,
        "Class 602" to R.drawable.sixthfloor_class602,
        "Class 603" to R.drawable.sixthfloor_class603,
        "Class 604" to R.drawable.sixthfloor_class604,
        "Class 605" to R.drawable.sixthfloor_class605,
        "Class 606" to R.drawable.sixthfloor_class606,
        "Class 607" to R.drawable.sixthfloor_class607,
        "Class 608" to R.drawable.sixthfloor_class608,
        "Class 609" to R.drawable.sixthfloor_class609,
        "Class 610" to R.drawable.sixthfloor_class610,
        "Class 611" to R.drawable.sixthfloor_class611,
        "Class 612" to R.drawable.sixthfloor_class612,
        "NSS Unit and DLLE Room" to R.drawable.sixthfloor_nssroom
    )

    private val adjacencyList = mapOf(
        "Class 601" to listOf("Class 602"),
        "Class 602" to listOf("Class 601", "Class 603"),
        "Class 603" to listOf("Class 602", "Class 604"),
        "Class 604" to listOf("Class 603", "Class 605"),
        "Class 605" to listOf("Class 604", "Class 606"),
        "Class 606" to listOf("Class 605", "NSS Unit and DLLE Room"),
        "NSS Unit and DLLE Room" to listOf("Class 606", "Class 607"),
        "Class 607" to listOf("NSS Unit and DLLE Room", "Class 608"),
        "Class 608" to listOf("Class 607", "Class 609"),
        "Class 609" to listOf("Class 608", "Class 610"),
        "Class 610" to listOf("Class 609", "Class 611"),
        "Class 611" to listOf("Class 610", "Class 612"),
        "Class 612" to listOf("Class 611", "Class 601")
    )

    private lateinit var nextImageButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        job = Job()

        pathImageView = findViewById(R.id.locationImage)

        nextImageButton = findViewById(R.id.nextbuton)
        nextImageButton.setOnClickListener {
            navigateToNextImage()
        }

        val startingLocation = intent.getStringExtra("starting_location") ?: ""
        val destination = intent.getStringExtra("destination") ?: ""

        launch {
            val shortestPath = findShortestPath(startingLocation, destination)
            path = shortestPath
            displayPathImages(path)
        }
    }

    private var currentPathIndex = 0

    private fun navigateToNextImage() {
        if (currentPathIndex < path.size - 1) {
            currentPathIndex++
            val nextImagePath = path[currentPathIndex]
            pathImageView.setImageResource(locationImages[nextImagePath] ?: 0)
        } else {
            val reached = R.drawable.destination
            pathImageView.setImageResource(reached)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun findShortestPath(
        startingPoint: String,
        destinationPoint: String
    ): List<String> = withContext(Dispatchers.Default) {
        val queue = PriorityQueue<Node>()
        val visited = mutableSetOf<String>()
        val parentMap = HashMap<String, String>()

        queue.add(Node(startingPoint, 0))

        while (queue.isNotEmpty()) {
            val current = queue.poll()

            if (current!!.location == destinationPoint) {
                // Found the destination, reconstruct the path
                val path = mutableListOf<String>()
                var node = current.location

                while (node != startingPoint) {
                    path.add(node)
                    node = parentMap[node] ?: break
                }

                path.add(startingPoint)
                return@withContext path.reversed()
            }

            if (current.location !in visited) {
                visited.add(current.location)
                for (neighbor in adjacencyList[current.location] ?: emptyList()) {
                    if (neighbor !in visited) {
                        val cost = current.cost + 1 // Assuming each step has a cost of 1

                        queue.add(Node(neighbor, cost))
                        parentMap[neighbor] = current.location
                    }
                }
            }
        }

        return@withContext emptyList()
    }

    private fun displayPathImages(path: List<String>) {
        val imageIds = path.map { locationImages[it] ?: 0 }
        imageIds.firstOrNull()?.let {
            pathImageView.setImageResource(it)
        }
    }

    private data class Node(val location: String, val cost: Int) : Comparable<Node> {
        override fun compareTo(other: Node): Int = cost.compareTo(other.cost)
    }
}
