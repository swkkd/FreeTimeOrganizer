package fto.ee.swk.freetimeorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    String[] data = {"All","music","family","theater","sport","film","festival","other","RANDOM"};

    public List<DataAdapter> ListOfdataAdapter;

    RecyclerView recyclerView;
    String HTTP_JSON_URL = "https://fto.ee/api/v1/events";
    final String Event_Id_JSON = "id";
    final String Event_Name_JSON = "title";
    final String Event_Date_JSON = "date";
    final String Event_End_Date_JSON = "end_date";
    final String Event_Time_JSON = "time";
    final String Event_Location_JSON = "location";
    final String Event_City_JSON = "city";
    final String Event_Country_JSON = "country";
    final String Event_Genre_JSON = "genre";
    final String Event_Info_JSON = "info";
    final String Event_Link_JSON = "link";
    final String Image_URL_JSON = "img";
    final String Event_Price_JSON = "price";
    final String Event_Description_JSON = "des";

    JsonArrayRequest RequestOfJSonArray;
    RequestQueue requestQueue;
    View view;
    int RecyclerViewItemPosition;
    RecyclerView.LayoutManager layoutManagerOfrecyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    ArrayList<JSONObject> EventDataHold;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        EventDataHold = new ArrayList<JSONObject>();

        ListOfdataAdapter = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        layoutManagerOfrecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);
        JSON_HTTP_CALL();

        // Pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);
                JSON_HTTP_CALL();
            }
        });

        // Implementing Click Listener on RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(HomeActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);
                if (view != null && gestureDetector.onTouchEvent(motionEvent)) {

                    Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
                    Bundle b = new Bundle();
                        b.putStringArray("details", new String[]{
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventName(),        // INDEX 0
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventDate(),        // INDEX 1
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventEndDate(),     // INDEX 2
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventTime(),        // INDEX 3
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventLocation(),    // INDEX 4
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventCity(),        // INDEX 5
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventCountry(),     // INDEX 6
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventGenre(),       // INDEX 7
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventInfo(),        // INDEX 8
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventLink(),        // INDEX 9
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventImageURL(),    // INDEX 10
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventPrice(),       // INDEX 11
                                ListOfdataAdapter.get(RecyclerViewItemPosition).getEventDescription(), // INDEX 12
                        });
                    intent.putExtras(b);
/*
EventName;
EventDate;
EventEndDate;
EventTime;
EventLocation;
EventCity;
EventCountry;
EventGenre;
EventInfo;
EventLink;
EventImageURL;
EventPrice;
EventID;
EventDescription;
*/
                    startActivity(intent);
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

        });
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Genre");
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events";
                        break;
                    case 1:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/1";
                        break;
                    case 2:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/3";
                        break;
                    case 3:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/2";
                        break;
                    case 4:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/4";
                        break;
                    case 5:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/6";
                        break;
                    case 6:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/5";
                        break;
                    case 7:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/genre/0";
                        break;
                    case 8:
                        HTTP_JSON_URL = "https://fto.ee/api/v1/events/random";
                        break;

                }
                JSON_HTTP_CALL();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void JSON_HTTP_CALL() {
        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ListOfdataAdapter.clear();
                        EventDataHold.clear();
                        ParseJSonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue = Volley.newRequestQueue(HomeActivity.this);

        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            DataAdapter GetDataAdapter2 = new DataAdapter();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setEventID(json.getString(Event_Name_JSON));
                GetDataAdapter2.setEventName(json.getString(Event_Name_JSON));
                GetDataAdapter2.setEventDate(json.getString(Event_Date_JSON));
                GetDataAdapter2.setEventEndDate(json.getString(Event_End_Date_JSON));
                GetDataAdapter2.setEventTime(json.getString(Event_Time_JSON));
                GetDataAdapter2.setEventLocation(json.getString(Event_Location_JSON));
                GetDataAdapter2.setEventCity(json.getString(Event_City_JSON));
                GetDataAdapter2.setEventCountry(json.getString(Event_Country_JSON));
                GetDataAdapter2.setEventGenre(json.getString(Event_Genre_JSON));
                GetDataAdapter2.setEventInfo(json.getString(Event_Info_JSON));
                GetDataAdapter2.setEventLink(json.getString(Event_Link_JSON));
                GetDataAdapter2.setEventImageURL(json.getString(Image_URL_JSON));
                GetDataAdapter2.setEventPrice(json.getString(Event_Price_JSON));
                GetDataAdapter2.setEventDescription(json.getString(Event_Description_JSON));


//                          TODO
//                ВНИМАНИЕ!!!!! КОСТЫЛЬ!!!!!
                if(json.getString(Image_URL_JSON).equals("null")){
                    GetDataAdapter2.setEventImageURL("https://pp.userapi.com/c622227/v622227481/ea18/Ac6o0JZhg6k.jpg");
                }
                else{
                    GetDataAdapter2.setEventImageURL(json.getString(Image_URL_JSON));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ListOfdataAdapter.add(GetDataAdapter2);
//           Log.e("Test_list",  ListOfdataAdapter.get(0).getEventDate());
        }
        recyclerViewadapter = new RecyclerViewAdapter(ListOfdataAdapter, this);
        recyclerView.setAdapter(recyclerViewadapter);
    }
}

