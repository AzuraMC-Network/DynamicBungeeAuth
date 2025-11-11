package net.uraharanz.plugins.dynamicbungeeauth.utils.apis;

import net.uraharanz.plugins.dynamicbungeeauth.DBABungeePlugin;
import net.uraharanz.plugins.dynamicbungeeauth.cache.apis.PlayerAPI;
import net.uraharanz.plugins.dynamicbungeeauth.utils.callback.CallbackAPI;

import java.util.UUID;

public class ProfileGenerator {
    private final DBABungeePlugin plugin;
    private final boolean MojangE;
    private final boolean CloudProtectedE;
    private final boolean MineToolsE;
    private final boolean BauxiteE;
    private static int number;

    public ProfileGenerator(DBABungeePlugin plugin) {
        this.plugin = plugin;
        this.MojangE = plugin.getConfigLoader().getBooleanCFG("APIS.Enable.Mojang.Enable");
        this.CloudProtectedE = plugin.getConfigLoader().getBooleanCFG("APIS.Enable.CloudProtected.Enable");
        this.MineToolsE = plugin.getConfigLoader().getBooleanCFG("APIS.Enable.MineTools.Enable");
        this.BauxiteE = plugin.getConfigLoader().getBooleanCFG("APIS.Enable.BauxiteAPI.Enable");
    }

    public void Generator(final String string, final CallbackAPI<UUID> callbackAPI) {
        this.plugin.getProxy().getScheduler().runAsync(DBABungeePlugin.plugin, () -> {
            block11: {
                try {
                    PlayerAPI playerAPI = this.plugin.getPlayerAPIList().searchRequest(string);
                    if (playerAPI != null) {
                        if (playerAPI.getUuid().equals("null")) {
                            callbackAPI.done(null);
                        } else {
                            callbackAPI.done(this.setDashedUUID(playerAPI.getUuid()));
                        }
                        break block11;
                    }
                    for (int i = 0; i < 4; ++i) {
                        if (number == 0) {
                            ++number;
                            if (!this.MojangE) continue;
                            ProfileAPIS.Mojang(string, new CallbackAPI<String>(){

                                @Override
                                public void done(String string2) {
                                    if (!string2.equals("null")) {
                                        PlayerAPI playerAPI = new PlayerAPI(string, string2);
                                        ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                        callbackAPI.done(ProfileGenerator.this.setDashedUUID(string2));
                                    } else {
                                        ProfileGenerator.this.fallbackAPI(string, DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("APIS.Enable.Mojang.Fallback"), new CallbackAPI<String>() {

                                            @Override
                                            public void done(String string) {
                                                if (string != null) {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, string);
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(ProfileGenerator.this.setDashedUUID(string));
                                                } else {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, "null");
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(null);
                                                }
                                            }

                                            @Override
                                            public void error(Exception exception) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                    exception.printStackTrace();
                                }
                            });
                            break;
                        }
                        if (number == 1) {
                            ++number;
                            if (!this.CloudProtectedE) continue;
                            ProfileAPIS.CloudProtected(string, new CallbackAPI<String>(){

                                @Override
                                public void done(String string2) {
                                    if (!string2.equals("null")) {
                                        PlayerAPI playerAPI = new PlayerAPI(string, string2);
                                        ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                        callbackAPI.done(ProfileGenerator.this.setDashedUUID(string2));
                                    } else {
                                        ProfileGenerator.this.fallbackAPI(string, DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("APIS.Enable.CloudProtected.Fallback"), new CallbackAPI<String>() {

                                            @Override
                                            public void done(String string) {
                                                if (string != null) {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, string);
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(ProfileGenerator.this.setDashedUUID(string));
                                                } else {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, "null");
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(null);
                                                }
                                            }

                                            @Override
                                            public void error(Exception exception) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                    exception.printStackTrace();
                                }
                            });
                            break;
                        }
                        if (number == 2) {
                            ++number;
                            if (!this.MineToolsE) continue;
                            ProfileAPIS.MineTools(string, new CallbackAPI<String>(){

                                @Override
                                public void done(String string2) {
                                    if (!string2.equals("null")) {
                                        PlayerAPI playerAPI = new PlayerAPI(string, string2);
                                        ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                        callbackAPI.done(ProfileGenerator.this.setDashedUUID(string2));
                                    } else {
                                        ProfileGenerator.this.fallbackAPI(string, DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("APIS.Enable.MineTools.Fallback"), new CallbackAPI<String>() {

                                            @Override
                                            public void done(String string) {
                                                if (string != null) {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, string);
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(ProfileGenerator.this.setDashedUUID(string));
                                                } else {
                                                    PlayerAPI playerAPI = new PlayerAPI(string, "null");
                                                    ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                    callbackAPI.done(null);
                                                }
                                            }

                                            @Override
                                            public void error(Exception exception) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void error(Exception exception) {
                                    exception.printStackTrace();
                                }
                            });
                            break;
                        }
                        if (number == 3) {
                            ++number;
                            if (this.BauxiteE) {
                                ProfileAPIS.BauxiteAPI(string, new CallbackAPI<String>(){

                                    @Override
                                    public void done(String string2) {
                                        if (!string2.equals("null")) {
                                            PlayerAPI playerAPI = new PlayerAPI(string, string2);
                                            ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                            callbackAPI.done(ProfileGenerator.this.setDashedUUID(string2));
                                        } else {
                                            ProfileGenerator.this.fallbackAPI(string, DBABungeePlugin.plugin.getConfigLoader().getIntegerCFG("APIS.Enable.BauxiteAPI.Fallback"), new CallbackAPI<String>() {

                                                @Override
                                                public void done(String string) {
                                                    if (string != null) {
                                                        PlayerAPI playerAPI = new PlayerAPI(string, string);
                                                        ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                        callbackAPI.done(ProfileGenerator.this.setDashedUUID(string));
                                                    } else {
                                                        PlayerAPI playerAPI = new PlayerAPI(string, "null");
                                                        ProfileGenerator.this.plugin.getPlayerAPIList().addRequest(playerAPI);
                                                        callbackAPI.done(null);
                                                    }
                                                }

                                                @Override
                                                public void error(Exception exception) {
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void error(Exception exception) {
                                        exception.printStackTrace();
                                    }
                                });
                                break;
                            }
                            number = 0;
                            continue;
                        }
                        number = 0;
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private UUID setDashedUUID(String string) {
        if (string != null) {
            String string2 = string.replace("\"", "");
            return UUID.fromString(string2.substring(0, 8) + "-" + string2.substring(8, 12) + "-" + string2.substring(12, 16) + "-" + string2.substring(16, 20) + "-" + string2.substring(20, 32));
        }
        return null;
    }

    private void fallbackAPI(String string, int n, final CallbackAPI<String> callbackAPI) {
        if (n == 1) {
            ProfileAPIS.Mojang(string, new CallbackAPI<String>(){

                @Override
                public void done(String string) {
                    if (!string.equals("null")) {
                        callbackAPI.done(string);
                    } else {
                        callbackAPI.done(null);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (n == 2) {
            ProfileAPIS.CloudProtected(string, new CallbackAPI<String>(){

                @Override
                public void done(String string) {
                    if (!string.equals("null")) {
                        callbackAPI.done(string);
                    } else {
                        callbackAPI.done(null);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (n == 3) {
            ProfileAPIS.MineTools(string, new CallbackAPI<String>(){

                @Override
                public void done(String string) {
                    if (!string.equals("null")) {
                        callbackAPI.done(string);
                    } else {
                        callbackAPI.done(null);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        } else if (n == 4) {
            ProfileAPIS.BauxiteAPI(string, new CallbackAPI<String>(){

                @Override
                public void done(String string) {
                    if (!string.equals("null")) {
                        callbackAPI.done(string);
                    } else {
                        callbackAPI.done(null);
                    }
                }

                @Override
                public void error(Exception exception) {
                }
            });
        }
    }
}
