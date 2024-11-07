package com.ghl7;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.ghl7.dao.SQLMapper;
import com.ghl7.instrument.BaseService;
import com.ghl7.receiving.BaseReceiving;
import com.ghl7.receiving.MT8000PlaceItem;
import com.ghl7.receiving.MT8000ReceiveResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainApplication {
    private final static String TAG = MainApplication.class.getSimpleName();
    private Json json;
    private AppRule appRule;
    private Channel channel;


    public MainApplication(){

        initialize();
        connectSqlServer();
        startInstrument();
    }

    private void initialize(){
        json = new Json();
        FileHandle ruleInternal = new FileHandle(Paths.APP_RULE_PATH.getPath());
        FileHandle channelInternal = new FileHandle(Paths.CHANNEL_PATH.getPath());
        appRule = json.fromJson(AppRule.class,ruleInternal);
        channel = json.fromJson(Channel.class,channelInternal);
        Paths.LOG_DIR.setPath(appRule.logDir);

        Logger.log(TAG,"Success to initialize;");
        Logger.log(TAG,"Channel size:midToLis:"+channel.midToLis.size());
        for (String key : channel.midToLis.keySet()) {
            Logger.log(TAG,key+":"+channel.midToLis.get(key));
        }
    }

    //这里不是真的连接数据库，而是将数据库信息传入，在要用到sql请求的时候会自动连接数据库
    private void connectSqlServer(){
        SQLMapper.setData(appRule.sqlUrl,appRule.userName,appRule.passwd);

        Logger.log(TAG,"Success to setData for SQLMapper,url:"+appRule.sqlUrl+",userName:"+appRule.userName);
    }

    private void startInstrument(){
        Logger.log(TAG,"Starting instrument...");
        List<BaseReceiving> receivings = new ArrayList<>();
        MT8000PlaceItem mt8000PlaceItem = new MT8000PlaceItem(appRule.mid,"ORM","O01",channel);
        MT8000ReceiveResults mt8000ReceiveResults = new MT8000ReceiveResults(appRule.mid,"ORU","R01",channel);
        receivings.add(mt8000PlaceItem);
        receivings.add(mt8000ReceiveResults);
//        BaseClient baseClient = new BaseClient(appRule.mid, appRule.startPort, appRule.useSTL,appRule.targetHost,appRule.targetPort,receivings);
//        baseClient.start();

        BaseService baseService = new BaseService(appRule.mid,appRule.startPort,appRule.useSTL,receivings);
        baseService.start();
    }
}
