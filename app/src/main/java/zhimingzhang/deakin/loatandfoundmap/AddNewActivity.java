package zhimingzhang.deakin.loatandfoundmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Arrays;

public class AddNewActivity extends AppCompatActivity {

    Button showonmap;
    Place selectedPlace;
    TextView placeName;
    Button saveButton;
    Button getLocButton;
    DatabaseHelper db;
    LocationManager lm;
    LocationListener ll;
    Location currentLocation;
    boolean SetCurrent;
    EditText name,phone,description,date,location;
    RadioButton found,lost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        showonmap = findViewById(R.id.showMapButton);
        placeName = findViewById(R.id.placeNameInput);
        saveButton = findViewById(R.id.saveButton);
        getLocButton = findViewById(R.id.getlocButton);
        SetCurrent = false;
        db = new DatabaseHelper(this);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyDbePzL-ukukMtNsEZPsSFZsH-NEnzl2uQ");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Toast.makeText(AddNewActivity.this, "Place: " + place.getName() + ", " + place.getLatLng(), Toast.LENGTH_SHORT).show();
                selectedPlace = place;
                SetCurrent = false;
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error
            }
        });

        showonmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SetCurrent){
                    String location_name = placeName.getText().toString();
                    double location_lat = currentLocation.getLatitude();
                    double location_lon = currentLocation.getLongitude();
                    Locations location = new Locations(location_name, location_lat, location_lon);
                    db.insertLocation(location);
                } else{
                    String location_name = placeName.getText().toString();
                    double location_lat = selectedPlace.getLatLng().latitude;
                    double location_lon = selectedPlace.getLatLng().longitude;
                    Locations location = new Locations(location_name, location_lat, location_lon);
                    db.insertLocation(location);
                }
            }
        });

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        ll = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                currentLocation = location;
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

        getLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetCurrent = true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}