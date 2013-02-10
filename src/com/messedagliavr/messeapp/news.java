package com.messedagliavr.messeapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class news extends ListActivity {

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}

	public static final String TITLE = "title";
	public static final String DESC = "description";
	public String[] titolim;
	public String[] descrizionim;
	ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mDialog = new ProgressDialog(news.this);
		mDialog.setMessage("Caricamento...");
		mDialog.setCancelable(false);
		super.onCreate(savedInstanceState);
		new connection().execute();
	}

	public class connection extends
			AsyncTask<Void, Void, HashMap<String, ArrayList<String>>> {
		public void onPreExecute() {
			mDialog.show();
		}

		public HashMap<String, ArrayList<String>> doInBackground(Void... params) {
			HashMap<String, ArrayList<String>> temhashmap = new HashMap<String, ArrayList<String>>();
			ArrayList<String> titoli = new ArrayList<String>();
			ArrayList<String> descrizioni = new ArrayList<String>();
			// All static variables
			final String URL = "http://www.messedaglia.it/index.php?option=com_ninjarsssyndicator&feed_id=1&format=raw";
			// XML node keys
			final String ITEM = "item"; // parent node
			final String TITLE = "title";
			final String DESC = "description";
			Element e = null;
			ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(URL); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(ITEM);

			// looping through all item nodes <item>
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				e = (Element) nl.item(i);
				// adding each child node to HashMap key => value
				map.put(TITLE, parser.getValue(e, TITLE));
				map.put(DESC, parser.getValue(e, DESC));
				// adding HashList to ArrayList
				menuItems.add(map);

			}

			for (int c = 0; c < nl.getLength(); c++) {
				e = (Element) nl.item(c);

				titoli.add(parser.getValue(e, TITLE));
				descrizioni.add(parser.getValue(e, DESC));
			}
			temhashmap.put("titoli", titoli);
			temhashmap.put("descrizioni", descrizioni);
			return temhashmap;
		}

		public void onPostExecute(HashMap<String, ArrayList<String>> resultmap) {

			if (resultmap.size() > 0) {
				// get titoli ArrayList here
				final ArrayList<String> titoli = resultmap.get("titoli");
				final ArrayList<String> descrizioni = resultmap
						.get("descrizioni");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						news.this, android.R.layout.simple_list_item_1, titoli);
				setContentView(R.layout.list_item);
				ListView listView = (ListView) news.this
						.findViewById(android.R.id.list);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parentView,
							View childView, int position, long id) {
						// Here write your code for starting the new activity on
						// selection of list item
						Intent intent = new Intent(news.this,
								ListItemSelected.class);
						String titolorw = titoli.get(position);
						String descrizionerw = descrizioni.get(position);
						Spanned titolo = Html.fromHtml(titolorw);
						Spanned descrizione = Html.fromHtml(descrizionerw);

						intent.putExtra(TITLE, titoli.get(position));
						intent.putExtra(DESC, descrizioni.get(position));
						startActivity(intent);
					}
				});
			}
			mDialog.dismiss();
		}
	}
}
