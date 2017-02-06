package com.androidnative.features.common;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;

public class AddressBookManager {

	private static AddressBookManager _instance = null;

	public static final String SEPARATOR_1 = "&#&";
	public static final String SEPARATOR_2 = "#&#";

	public static AddressBookManager GetInstance() {
		if (_instance == null) {
			_instance = new AddressBookManager();
		}

		return _instance;
	}
	


	@SuppressLint("InlinedApi") 
	public void load() {
		Context context = NativeUtility.GetApplicationContex();
		String[] titles = new String[] { "Name ", "Phone ", "Email ", "Note ", "Chat ", "Organization ", "Photo ", "Address " };
		List<String> names = new ArrayList<String>();
		StringBuilder result = new StringBuilder();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		ContentResolver content_resolver = context.getContentResolver();
		Cursor cur = content_resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				result.append(titles[0].concat(name));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// PHONE//
					String phone = "";
					Cursor cursor_phone = content_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ", new String[] { id }, null);

					if (cursor_phone.moveToFirst()) {
						phone = cursor_phone.getString(cursor_phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					result.append(SEPARATOR_1);
					result.append(titles[1]);
					result.append(phone == null || phone.equals("") ? "-" : phone);

					cursor_phone.close();
					// EMAIL//
					String email = "", emailType = "";
					Cursor emailCur = content_resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);

					if (emailCur.moveToNext()) {
						email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
					}
					result.append(SEPARATOR_1);
					result.append(titles[2]);
					result.append(email == null || email.equals("") ? "-" : email.concat(SEPARATOR_2).concat(emailType == null || emailType.equals("") ? "-" : emailType));

					emailCur.close();
					// NOTE//
					String note = "";
					String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
					String[] noteWhereParams = new String[] { id, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE };
					Cursor noteCur = content_resolver.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);

					if (noteCur.moveToFirst()) {
						note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
					}
					result.append(SEPARATOR_1);
					result.append(titles[3]);
					result.append(note.equals(null) || note.equals("") ? "-" : note);

					noteCur.close();
					// CHAT//
					String imName = "", imType = "";
					String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
					String[] imWhereParams = new String[] { id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE };
					Cursor chatCur = content_resolver.query(ContactsContract.Data.CONTENT_URI, null, imWhere, imWhereParams, null);

					if (chatCur.moveToFirst()) {
						imName = chatCur.getString(chatCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
						imType = chatCur.getString(chatCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
					}
					result.append(SEPARATOR_1);
					result.append(titles[4]);
					result.append(imName == null || imName.equals("") ? "-" : imName.concat(SEPARATOR_2).concat(imType == null || imType.equals("") ? "-" : imType));

					chatCur.close();
					// ORGANIZATION//
					String orgName = "", title = "";
					String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
					String[] orgWhereParams = new String[] { id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE };
					Cursor orgCur = content_resolver.query(ContactsContract.Data.CONTENT_URI, null, orgWhere, orgWhereParams, null);

					if (orgCur.moveToFirst()) {
						orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
						title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
					}
					result.append(SEPARATOR_1);
					result.append(titles[5]);
					result.append(orgName == null || orgName.equals("") ? "-" : orgName.concat(SEPARATOR_2).concat(title == null || title.equals("") ? "-" : title));

					orgCur.close();
					// PHOTO//
					String photo_data = "";
					Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(id));
					Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
					Cursor photo_cursor = content_resolver.query(photoUri, new String[] { Contacts.Photo.PHOTO }, null, null, null);
					if (photo_cursor.moveToFirst()) {
						byte[] data = photo_cursor.getBlob(0);
						for (int i = 0; i < data.length; i++) {
							if (i != 0) {
								photo_data += (",");
							}
							photo_data += String.valueOf(data[i]);
						}
					}
					result.append(SEPARATOR_1);
					result.append(titles[6]);
					result.append(photo_data == null || photo_data.equals("") ? "-" : photo_data);

					photo_cursor.close();
					// GET POSTAL ADDRESS//
					String poBox = "", street = "", city = "", state = "", postalCode = "", country = "", type = "";
					String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
					String[] addrWhereParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
					Cursor addressCur = content_resolver.query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);

					if (addressCur.moveToNext()) {
						poBox = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
						street = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
						city = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
						state = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
						postalCode = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
						country = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
						type = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
					}
					result.append(SEPARATOR_1);
					result.append(titles[7]);
					result.append(poBox == null || poBox.equals("") ? "-" : poBox).append(SEPARATOR_2);
					result.append(street == null || street.equals("") ? "-" : street).append(SEPARATOR_2);
					result.append(city == null || city.equals("") ? "-" : city).append(SEPARATOR_2);
					result.append(state == null || state.equals("") ? "-" : state).append(SEPARATOR_2);
					result.append(postalCode == null || postalCode.equals("") ? "-" : postalCode).append(SEPARATOR_2);
					result.append(country == null || country.equals("") ? "-" : country).append(SEPARATOR_2);
					result.append(type == null || type.equals("") ? "-" : type).append(SEPARATOR_2);

					addressCur.close();
				}

				result.append("|");
				names.add(result.toString());
			}

		}

		cur.close();

		UnityPlayer.UnitySendMessage("AddressBookController", "OnContactsLoaded", result.toString());
	}
}
