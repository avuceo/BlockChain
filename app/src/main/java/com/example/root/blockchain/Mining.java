package com.example.root.blockchain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class Mining extends AppCompatActivity {
DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
DatabaseHelperNewNode mydb;
TransBlock newnode;
    public static ArrayList<TransBlock> blockchain =new ArrayList<TransBlock>();


    TransBlock NodeToAdd;
    DatabaseHelperTrans ne;
    FirebaseStorage storage;
    StorageReference storageRef,imageRef,DownRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    public Uri down;
    DatabaseReference fb;
    Uri selectedImage;
    public static  String ak;
int Only=0;

    CircleImageView circleImageView;
    TextView req,reqsender,Nonce,noncefinder;

    Button startMining;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;


    int NvaisOpen=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);
        mydb = new DatabaseHelperNewNode(Mining.this);
        ne=new DatabaseHelperTrans(Mining.this);

        req=findViewById(R.id.textView6);
        reqsender=findViewById(R.id.textView8);
        noncefinder=findViewById(R.id.textView11);
        Nonce=findViewById(R.id.textView13);
        startMining=findViewById(R.id.button2);

        setTitle("Mining");



      PulsatorLayout  pulsator=findViewById(R.id.pulsator);
      pulsator.start();

        startMining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startMining.setText("Mining Started!");
                req.setVisibility(View.VISIBLE);
                reqsender.setVisibility(View.VISIBLE);
                noncefinder.setVisibility(View.VISIBLE);
                Nonce.setVisibility(View.VISIBLE);


                GetBlocChain();








            }
        });




        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.common_open_on_phone, R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);

        nv.setItemIconTintList(null);

nv.bringToFront();

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {



                int id = menuItem.getItemId();
                switch(id)
                {
                    case R.id.BlockChain:
                        blockchainintent();
                        return  true;

                    case R.id.MyWallet:

                        MyWalletintent();return  true;


                    case R.id.SendMoney:

                        sendtominerintent();return  true;


                    case R.id.Mining:

                        miningintent();return  true;


                    default:
                        return false;
                }


            }
        });






















        // GetBlocChain();


    }

    public  int GetNode(String url,String name){




        PRDownloader.initialize(getApplicationContext());

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

// Setting timeout globally for the download network requests:
        config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        int downloadId = PRDownloader.download(url, "/mnt/sdcard/",name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                }).start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                      //  Toast.makeText(getBaseContext(),"A",Toast.LENGTH_SHORT).show();




                    }

                    @Override
                    public void onError(Error error) {

                    }
                });















return 0;




    }
    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }



    public void GetBlocChain(){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("ActiveChain").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(Only==0) {
                   GetNode(dataSnapshot.getValue().toString(), "blockchain.db");
                   extractchain();
Only++;
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


















    }

    private void extractchain() {
        DatabaseHelperTrans myblockchain=new DatabaseHelperTrans(Mining.this);
        Cursor res = myblockchain.getAllData();
        if(res.getCount() == 0) {
        }
        while (res.moveToNext()) {
            String Position=res.getString(0);
            String Sender=res.getString(1);
            String Reciver=res.getString(2);
            String Value=res.getString(3);
            String Hash=res.getString(4);
            String PrevHash=res.getString(5);
            String timestamp=res.getString(6);
            String nonce=res.getString(7);


            StringBuffer buffer = new StringBuffer();

            buffer.append(Position+"\n");
            buffer.append(Sender+"\n");
            buffer.append(Reciver+"\n");
            buffer.append(Value+"\n");
            buffer.append(Hash+"\n");
            buffer.append(PrevHash+"\n");
            buffer.append(timestamp+"\n");
            buffer.append(nonce+"\n");




           newnode=new TransBlock(Position,Sender,Reciver,Value,Hash,PrevHash,timestamp,nonce,"");

            Log.d("A", "extractchain: "+buffer.toString());
           blockchain.add(newnode );
int s=blockchain.size()-1;
ak=blockchain.get(s).getHash();


        }

       //Toast.makeText(getBaseContext(),"BLOCKCHAIN FETCHED",Toast.LENGTH_SHORT).show();

      DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();


        databaseReference.child("MiningRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             if(dataSnapshot.exists()){
                 MiningReq();

             }
             else {
               // Toast.makeText(getBaseContext(),"NO MINING REQUEST",Toast.LENGTH_SHORT).show();

             }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }









    public  void MiningReq(){



        databaseReference.child("MiningRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                GetNode(dataSnapshot.getValue().toString(),"newnode.db");
                reqsender.setText(dataSnapshot.getKey().toString());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {



            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        extractNode();



    }


    private void extractNode() {
        DatabaseHelperNewNode myblockchain=new DatabaseHelperNewNode(Mining.this);
        String Position = null,Sender=null,Reciver=null,Value=null,Hash=null,PrevHash=null,timestamp=null,nonce=null;
        Cursor res = myblockchain.getAllData();
        if(res.getCount() == 0) {
        }
        while (res.moveToNext()) {
             Position=res.getString(0);
             Sender=res.getString(1);
             Reciver=res.getString(2);
             Value=res.getString(3);
             Hash=res.getString(4);
             PrevHash=res.getString(5);
             timestamp=res.getString(6);
             nonce=res.getString(7);


            StringBuffer buffer = new StringBuffer();

            buffer.append(Position+"\n");
            buffer.append(Sender+"\n");
            buffer.append(Reciver+"\n");
            buffer.append(Value+"\n");
            buffer.append(Hash+"\n");
            buffer.append(PrevHash+"\n");
            buffer.append(timestamp+"\n");
            buffer.append(nonce+"\n");


        }


        //Log.d("A", "extractNode: "+buffer.toString());
        NodeToAdd=new TransBlock(Position,Sender,Reciver,Value,Hash,PrevHash,timestamp,nonce,"");

        FindingNonce(NodeToAdd,blockchain.size()+"",Sender,Reciver);



    }
    public void FindingNonce(TransBlock NodeToAdd,String prev,String SEnder,String Recvicer){

TransBlock BLocklast=blockchain.get(blockchain.size()-1);


        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();


        int nonce=0;

String DHash=newnode.getSender()+newnode.getReciver()+newnode.getValue()+BLocklast.getHash()+ts+nonce;

        Log.d("A", "FindingNonce: "+newnode.getSender()+"\n"+newnode.getReciver()+"\n"+newnode.getValue());
      String Hash=md5(DHash);

        while (!Hash.substring(0,3).contains("000"))
        {



            nonce++;
            Log.d("a", ""+nonce);
             DHash=newnode.getSender()+newnode.getReciver()+newnode.getValue()+BLocklast.getHash()+ts+nonce;
             Hash=md5(DHash);
Nonce.setText(nonce+"");

        }


ne.insertData(SEnder,SEnder,"20",Hash,ak,ts, String.valueOf(nonce),logo.myInfo.getUid());

        selectedImage = Uri.fromFile(new File("/mnt/sdcard/blockchain.db"));
        uploadImage();







/*
        TransBlock newlycreated=new TransBlock(String.valueOf(blockchain.size()),newnode.getSender(),newnode.getReciver(),newnode.getValue(),Hash,blockchain.get(blockchain.size()-1).getHash(),ts,String.valueOf(nonce));

   blockchain.add(newlycreated);*/





      //  boolean a=ne.insertData("","","","","","","");
       /* boolean ab=ne.insertData(blockchain.get(blockchain.size()-1).getSender(),blockchain.get(blockchain.size()-1).getReciver()
                ,blockchain.get(blockchain.size()-1).getValue(),blockchain.get(blockchain.size()-1).getHash(),
                blockchain.get(blockchain.size()-1).getPrevHash(),blockchain.get(blockchain.size()-1).getTimestamp(),blockchain.get(blockchain.size()-1).getNonce());

*/






//
      ///  selectedImage = Uri.fromFile(new File("/mnt/sdcard/blockchain.db"));
     //   uploadImage();



//getRewarded("","","","","","","");
//getRewarded(newlycreated.getSender(),newlycreated.getReciver(),newlycreated.getValue(),newlycreated.getHash(),newlycreated.getPrevHash(),

     //   newlycreated.getTimestamp(), newlycreated.getNonce());

    }

    private void getRewarded(String A,String B,String C,String D,String E,String F,String G) {

   TransBlock BLocklast=blockchain.get(blockchain.size()-1);
        TransBlock newnode=new TransBlock(String.valueOf(blockchain.size()),"Reward","c",String.valueOf(1),"","","","","");


        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();


        int nonce=0;

        String DHash=newnode.getSender()+newnode.getReciver()+newnode.getValue()+BLocklast.getHash()+ts+nonce;


        String Hash=md5(DHash);


        while (!Hash.substring(0,3).contains("000"))
        {



            nonce++;
            Log.d("a", ""+nonce);
            DHash=newnode.getSender()+newnode.getReciver()+newnode.getValue()+BLocklast.getHash()+ts+nonce;
            Hash=md5(DHash);


        }
/*


boolean a=ne.insertData(blockchain.get(blockchain.size()-1).getSender(),blockchain.get(blockchain.size()-1).getReciver()
        ,blockchain.get(blockchain.size()-1).getValue(),blockchain.get(blockchain.size()-1).getHash(),
        blockchain.get(blockchain.size()-1).getPrevHash(),blockchain.get(blockchain.size()-1).getTimestamp(),blockchain.get(blockchain.size()-1).getNonce());
*/

/*

if(a==true) {
   DatabaseHelperTrans databaseHelperTrans=new DatabaseHelperTrans(Mining.this);
    databaseHelperTrans.insertData(newnode.getSender(), newnode.getReciver(), newnode.getValue(), Hash, D,

            ts, String.valueOf(nonce));

}

*/















/*

   Boolean b=    ne.insertData("Reward", "c", String.valueOf(1), Hash, blockchain.get(blockchain.size()-1).getHash(),

                ts, String.valueOf(nonce));
*/
/*



if(b==true){

    selectedImage = Uri.fromFile(new File("/mnt/sdcard/blockchain.db"));
    uploadImage();




}
*/





    }


    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }


    public void uploadImage() {
        storageRef= FirebaseStorage.getInstance().getReference();

        imageRef = storageRef.child("blockchain"+"/"+"blockchain.db");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = imageRef.putFile(selectedImage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new com.google.firebase.storage.OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getBaseContext(),"Error in uploading!",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //   down = taskSnapshot.getDownloadUrl();


                Toast.makeText(getBaseContext(),"Upload successful",Toast.LENGTH_SHORT).show();


                progressDialog.dismiss();
                DownRef = storageRef.child("blockchain"+"/"+"blockchain.db"+"/");
                DownRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("ActiveChain").setValue(uri.toString());

                        databaseReference.child("MiningRequest").removeValue();







                    }
                });
            }
        });

    }

    private void MyWalletintent() {


        Intent intent=new Intent(Mining.this,MyWallet.class);
        startActivity(intent);
    }

    private void blockchainintent() {


        Intent intent=new Intent(Mining.this,BlockChain2.class);
        startActivity(intent);
    }

    private void miningintent() {


        Intent intent=new Intent(Mining.this,Mining.class);
        startActivity(intent);
    }
    private void sendtominerintent() {


        Intent intent=new Intent(Mining.this,SendToMiner.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navDrawerOpen();
    if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


  public void   navDrawerOpen(){


        NvaisOpen++;
        if(NvaisOpen%2==0){

            findViewById(R.id.button2).setVisibility(View.VISIBLE);
            findViewById(R.id.mini).setVisibility(View.VISIBLE);
        }else
      {
            findViewById(R.id.button2).setVisibility(View.INVISIBLE);
          findViewById(R.id.mini).setVisibility(View.INVISIBLE);
      }


    }

}






