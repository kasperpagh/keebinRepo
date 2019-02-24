package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import entity.CoffeeBrand;
import entity.UserLocation;
import entity.Token;
import utils.image.BitmapToByteConverter;
import utils.image.ByteArrayToBitmapConverter;

/**
 * Created by mrlef on 12/4/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper
{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KeebinDB";

    // CoffeeBrands table name
    private static final String TABLE_COFFEEBRAND = "coffeeBrand";
    private static final String TABLE_TOKENS = "tokens";
    private static final String TABLE_LOCATION = "location";
    private static final String TABLE_BRANDPICTURES = "brandPictures";
    private static final String TABLE_SHOPPICTURES = "shopPictures";
    private static final String TABLE_DBVERSION = "databaseVersion";

    // CoffeeBranch Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "brandName";
    private static final String KEY_DATABASEID = "dataBaseId";
    private static final String KEY_NUMBEROFCOFFEENEEDED = "numberOfCoffeeNeeded";


    //Token columns names
    private static final String KEY_TOKENNAME = "tokenName";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USEREMAIL = "email";

    //UserLocation table columns names
    private static final String KEY_LASTKNOWLOCATIONLAT = "lastKnowLocationLat";
    private static final String KEY_LASTKNOWLOCATIONLNG = "lastKnowLocationLng";
    private static final String KEY_USERID = "userId";

    //brandPictures column names
    private static final String KEY_BRANDNAME = "brandName";
    private static final String KEY_IMAGEDATA = "imageData";

    //shopPictures column names
    private static final String KEY_SHOPEMAIL = "shopEmail";
    private static final String KEY_SHOPIMAGEDATA = "shopImageData";

    //databaseVersion column names
    private static final String KEY_DBVERSIONPRIMARYKEY = "dbVersPrimaryKey";
    private static final String KEY_CURRENTDBVERSION = "currentDbVersion";

    private String coffeeBrandCreationString;
    private String tokenCreationString;
    private String locationCreationString;
    private String brandPictureCreationString;
    private String shopImageCreationString;
    private String dbVersionCreationString;


    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        coffeeBrandCreationString = "CREATE TABLE " + TABLE_COFFEEBRAND + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATABASEID + " INTEGER," + KEY_NUMBEROFCOFFEENEEDED + ");"; // INTEGER," + "UNIQUE(" + KEY_NAME + ") ON CONFLICT IGNORE
        //sat op så hvis der forsøges at tilføje et CoffeeBrand med samme navn så ignorerer den det.


        tokenCreationString = "CREATE TABLE " + TABLE_TOKENS
                + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TOKENNAME + " TEXT, "
                + KEY_TOKEN + " TEXT, "
                + KEY_USEREMAIL + " TEXT, "
                + "UNIQUE( " + KEY_TOKENNAME + " ) ON CONFLICT IGNORE );";


        locationCreationString = "CREATE TABLE " + TABLE_LOCATION + "(" + KEY_USERID + " INTEGER PRIMARY KEY, " + KEY_LASTKNOWLOCATIONLAT + " REAL, " + KEY_LASTKNOWLOCATIONLNG + " REAL, " + "UNIQUE(" + KEY_USERID + ") ON CONFLICT REPLACE);";


        brandPictureCreationString = "CREATE TABLE " + TABLE_BRANDPICTURES + "(" + KEY_BRANDNAME + " TEXT PRIMARY KEY, " + KEY_IMAGEDATA + " BLOB, " + "UNIQUE(" + KEY_BRANDNAME + ") ON CONFLICT IGNORE);";


        shopImageCreationString = "CREATE TABLE " + TABLE_SHOPPICTURES + "(" + KEY_SHOPEMAIL + " TEXT PRIMARY KEY, " + KEY_SHOPIMAGEDATA + " BLOB, " + "UNIQUE(" + KEY_SHOPEMAIL + ") ON CONFLICT REPLACE);";

        dbVersionCreationString = "CREATE TABLE " + TABLE_DBVERSION + "(" + KEY_DBVERSIONPRIMARYKEY + " INTEGER PRIMARY KEY, " + KEY_CURRENTDBVERSION + " INTEGER, " + "UNIQUE(" + KEY_DBVERSIONPRIMARYKEY + ") ON CONFLICT REPLACE);";

        db.execSQL(tokenCreationString);
        db.execSQL(coffeeBrandCreationString);
        db.execSQL(locationCreationString);
        db.execSQL(brandPictureCreationString);
        db.execSQL(shopImageCreationString);
        db.execSQL(dbVersionCreationString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COFFEEBRAND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKENS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDPICTURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPICTURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DBVERSION);


        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    //DB VERSION - base value is one!
    public void addOrUpdateDbVersion(int dbVersionNumberFromServer)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DBVERSIONPRIMARYKEY, 1);
        values.put(KEY_CURRENTDBVERSION, dbVersionNumberFromServer);
        db.insertWithOnConflict(TABLE_DBVERSION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public int getDatabaseVersion()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DBVERSION, new String[]{KEY_DBVERSIONPRIMARYKEY, KEY_CURRENTDBVERSION}, KEY_DBVERSIONPRIMARYKEY + "=?", new String[]{String.valueOf(1)}, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToPosition(0);
        }

        return cursor.getInt(1);
    }


    //CRUD SHOP PICTURES
    //ADD SHOPPICTURE
    public void addShopPicture(Bitmap bm, String shopEmail)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SHOPEMAIL, shopEmail.toLowerCase());
        values.put(KEY_SHOPIMAGEDATA, BitmapToByteConverter.getBytes(bm));
        db.insert(TABLE_SHOPPICTURES, null, values);
        db.close();
    }


    public Bitmap getShopPicture(String shopEmail)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        shopEmail = shopEmail.toLowerCase();
        Cursor cursor = db.query(TABLE_SHOPPICTURES, new String[]{KEY_SHOPEMAIL, KEY_SHOPIMAGEDATA}, KEY_SHOPEMAIL + "=?", new String[]{String.valueOf(shopEmail)}, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToPosition(0);
        }
        Bitmap bm = ByteArrayToBitmapConverter.getImage(cursor.getBlob(1));
        db.close();
        return bm;
    }

    public int updateShopPicture(Bitmap bm, String shopEmail)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_SHOPIMAGEDATA, BitmapToByteConverter.getBytes(bm));
            return db.update(TABLE_SHOPPICTURES, values, KEY_SHOPEMAIL + "=?", new String[]{String.valueOf(shopEmail)});
        }
        finally
        {
            db.close();
        }

    }


    //CRUD BRAND PICTURES
    //ADD BRANDPICTURE
    public void addBrandPicture(Bitmap bm, String brandName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_BRANDNAME, brandName.toLowerCase());
            values.put(KEY_IMAGEDATA, BitmapToByteConverter.getBytes(bm));
            db.insert(TABLE_BRANDPICTURES, null, values);
        }
        finally
        {
            db.close();
        }

    }

    public Bitmap getBrandPicture(String brandName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            brandName = brandName.toLowerCase();
            Cursor cursor = db.query(TABLE_BRANDPICTURES, new String[]{KEY_BRANDNAME, KEY_IMAGEDATA}, KEY_BRANDNAME + "=?", new String[]{String.valueOf(brandName)}, null, null, null, null);
            if (cursor != null)
            {
                cursor.moveToPosition(0);
            }
            Bitmap bm = ByteArrayToBitmapConverter.getImage(cursor.getBlob(1));
            return bm;
        }
        finally
        {
            db.close();
        }

    }

    public int updateBrandPicture(Bitmap bm, String brandName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {

            ContentValues values = new ContentValues();
            values.put(KEY_IMAGEDATA, BitmapToByteConverter.getBytes(bm));
            return db.update(TABLE_BRANDPICTURES, values, KEY_BRANDNAME + "=?", new String[]{String.valueOf(brandName)});
        }
        finally
        {
            db.close();
        }

    }


    //LOCATION CRU (no delete needed)
    public void addLocation(UserLocation userLocation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_USERID, 1);
            values.put(KEY_LASTKNOWLOCATIONLAT, userLocation.getLat());
            values.put(KEY_LASTKNOWLOCATIONLNG, userLocation.getLng());

            db.insertWithOnConflict(TABLE_LOCATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            Log.d("DBH", "ADD LOC ER KALDT, loc=" + values.toString());
        }
        finally
        {
            db.close();
        }

    }

    public UserLocation getLocation(int userID)
    {
        Log.d("DBH", "Get loc; begynder getLocation from DB");
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            Cursor cursor = db.query(TABLE_LOCATION, new String[]{KEY_USERID, KEY_LASTKNOWLOCATIONLAT, KEY_LASTKNOWLOCATIONLNG}, KEY_USERID + "=?", new String[]{String.valueOf(userID)}, null, null, null, null);
            if (cursor != null)
            {
                cursor.moveToPosition(0);
//            cursor.moveToFirst();
            }
            //input is (in order) userID, lat, lng
            UserLocation userLocation;

            try
            {
                userLocation = new UserLocation(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2));
            }
            catch (CursorIndexOutOfBoundsException e)
            {
                userLocation = new UserLocation(0, 0);
            }


            Log.d("DBH", "Get loc; Returnere location");
            return userLocation;
        }
        finally
        {
            db.close();
        }
    }

    public int updateLocation(UserLocation userLocation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DBH", "UPDATE LOC ER KALDT, loc=" + userLocation.toString());
        try
        {

            ContentValues values = new ContentValues();
            values.put(KEY_USERID, 1);
            values.put(KEY_LASTKNOWLOCATIONLAT, userLocation.getLat());
            values.put(KEY_LASTKNOWLOCATIONLNG, userLocation.getLng());
            Log.d("DBH", "UPD LOC ER KALDT, loc er gemt");
            return db.update(TABLE_LOCATION, values, KEY_USERID + "=?", new String[]{String.valueOf(userLocation.getUserId())});

        }
        finally
        {
            db.close();
        }
    }

    // Adding new coffeeBrand
    public void addCoffeeBrand(CoffeeBrand coffeeBrand)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_DATABASEID, coffeeBrand.getId());
            values.put(KEY_NAME, coffeeBrand.getBrandName()); // CoffeeBrand Name
            values.put(KEY_NUMBEROFCOFFEENEEDED, coffeeBrand.getNumberOfCoffeeNeeded()); // CoffeeBrand coffees needed


            // Inserting Row
            db.insert(TABLE_COFFEEBRAND, null, values);
        }
        finally
        {
            db.close(); // Closing database connection
        }

    }

    // Getting single CoffeeBrand on client DB ID
    public CoffeeBrand getBrandbyId(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            Cursor cursor = db.query(TABLE_COFFEEBRAND, new String[]{KEY_ID, KEY_NAME, KEY_NUMBEROFCOFFEENEEDED, KEY_DATABASEID}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
            }

            CoffeeBrand cb = new CoffeeBrand(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));

            // return contact
            return cb;
        }
        finally
        {
            db.close();
        }
    }

    // Getting single CoffeeBrand on Server DB ID
    public CoffeeBrand getBrandbyServerId(int brandName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            Cursor cursor = db.query(TABLE_COFFEEBRAND, new String[]{KEY_ID, KEY_NAME, KEY_NUMBEROFCOFFEENEEDED, KEY_DATABASEID}, KEY_DATABASEID + "=?", new String[]{String.valueOf(brandName)}, null, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
            }

            CoffeeBrand cb = new CoffeeBrand(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));

            // return contact
            return cb;
        }
        finally
        {
            db.close();
        }
    }

    // Getting All CoffeeBrands
    public List<CoffeeBrand> getAllCoffeeBrands()
    {
        List<CoffeeBrand> coffeeBrandList = new ArrayList<CoffeeBrand>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COFFEEBRAND;

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst())
            {
                do
                {
                    CoffeeBrand cb = new CoffeeBrand();
                    cb.setId(Integer.parseInt(cursor.getString(0)));
                    cb.setBrandName(cursor.getString(1));
                    cb.setDataBaseId(Integer.parseInt(cursor.getString(2)));
                    cb.setNumberOfCoffeeNeeded(Integer.parseInt(cursor.getString(3)));

                    // Adding contact to list
                    coffeeBrandList.add(cb);
                }
                while (cursor.moveToNext());
            }

            // return contact list

            return coffeeBrandList;
        }
        finally
        {
            db.close();
        }
    }


    // Updating single CoffeeBrand
    public int updateCoffeeBrand(String brandName, int coffeesNeeded, int brandId, int dataBaseId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, brandName);
            values.put(KEY_NUMBEROFCOFFEENEEDED, coffeesNeeded);
            values.put(KEY_DATABASEID, dataBaseId);

            // updating row
            return db.update(TABLE_COFFEEBRAND, values, KEY_ID + "=?", new String[]{String.valueOf(brandId)});
        }
        finally
        {
            db.close();
        }
    }


    // Deleting single CoffeeBrand
    public void deleteCoffeeBrandById(int brandId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.delete(TABLE_COFFEEBRAND, KEY_ID + "=?", new String[]{String.valueOf(brandId)});
        }
        finally
        {
            db.close();
        }

    }


    //Tokens CRUD
    // Adding new Token
    public void addToken(Token token)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_TOKENNAME, token.getName());
            values.put(KEY_TOKEN, token.getTokenData());


            // Inserting Row
            db.insert(TABLE_TOKENS, null, values);
        }
        finally
        {
            db.close(); // Closing database connection
        }

    }

    // Getting single Token on client DB ID
    public Token getTokenByName(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            Cursor cursor = db.query(TABLE_TOKENS, new String[]{KEY_ID, KEY_TOKENNAME, KEY_TOKEN}, KEY_TOKENNAME + "=?", new String[]{name}, null, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
            }

            Token t = new Token(cursor.getInt(0), cursor.getString(1), cursor.getString(2));

            // return contact
            return t;
        }
        finally
        {
            db.close();
        }
    }


    // Getting All CoffeeBrands
    public List<Token> getAllTokens()
    {
        List<Token> tokenList = new ArrayList<Token>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TOKENS;

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {


            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst())
            {
                do
                {
                    Token t = new Token();
                    t.setId(Integer.parseInt(cursor.getString(0)));
                    t.setName(cursor.getString(1));
                    t.setTokenData(cursor.getString(2));

                    // Adding contact to list
                    tokenList.add(t);
                }
                while (cursor.moveToNext());
            }

            // return contact list

            return tokenList;
        }
        finally
        {
            db.close();
        }
    }


    // Updating single CoffeeBrand
    public int updateToken(String tokenName, String tokenData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_TOKENNAME, tokenName);
            values.put(KEY_TOKEN, tokenData);

            // updating row
            return db.update(TABLE_TOKENS, values, KEY_TOKENNAME + "=?", new String[]{tokenName});
        }
        finally
        {
            db.close();
        }
    }


    // Deleting single CoffeeBrand
    public void deleteToken(String tokenName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.delete(TABLE_TOKENS, KEY_TOKENNAME + "=?", new String[]{tokenName});
        }
        finally
        {
            db.close();
        }

    }


    public boolean hasObject(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            String selectString = "SELECT * FROM " + TABLE_TOKENS + " WHERE " + KEY_TOKENNAME + " =?";
            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[]{id});

            boolean hasObject = false;
            if (cursor.moveToFirst())
            {
                hasObject = true;

                //region if you had multiple records to check for, use this region.

                int count = 0;
                while (cursor.moveToNext())
                {
                    count++;
                }
                //here, count is records found
                //endregion
            }

            cursor.close();          // Dont forget to close your cursor
            //AND your Database!
            return hasObject;
        }
        finally
        {
            db.close();
        }


    }


}
