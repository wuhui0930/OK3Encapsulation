# OK3Encapsulation
基础封装

private OkHttpUtils okHttpUtils;

okHttpUtils=OkHttpUtils.getInstance();

okHttpUtils.asynJsonUrl("url", new OkHttpUtils.OutPutJson() {
            @Override
            public void onResponse(String result) {
                //....
            }
        });
