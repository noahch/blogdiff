package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.EditTreeUtils;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
public class PreprocessingAnalysis {


    @Autowired
    private TravisService travisService;

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @RequestMapping("/analysis/verifyPreprocessing")
    public void verifyPreprocessing(){
        String[][] jobs =  {
                            {"526313909","527008457"}, //1
                            {"526323134","527009122"}, //2
                            {"526898193","527009313"}, //3
                            {"526900824","526905479"}, //4
                            {"526910311","526911622"}, //5
                            {"526922964","526923460"}, //6
                            {"526918182","526919514"}, //7
                            {"526923320","526930055"}, //8
                            {"526931972","526937400"}, //9
                            {"526932987","527009658"}, //10
                            };

        StringBuilder sb = new StringBuilder();
        sb.append("Type;Number;Log\n");

        for(int i = 0; i < jobs.length; i++){
            String j1 = jobs[i][0];
            String j2 = jobs[i][1];
            log.info("Started with " +j1+"_"+j2+" no filter");
            double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
            double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
            double length = max(l1,l2);

            BuildLogTree tree1 = travisService.getBuildLogTree(j1,false);
            BuildLogTree tree2 = travisService.getBuildLogTree(j2,false);
            EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());

            double a = EditTreeUtils.getAdditions(editTree);
            double d = EditTreeUtils.getDeletions(editTree);
            double m = EditTreeUtils.getMoves(editTree);
            double u = EditTreeUtils.getUpdates(editTree);
            length += max(a,d);

            sb.append("AB;"+a/length+";"+j1+"_"+j2+"\n");
            sb.append("DB;"+d/length+";"+j1+"_"+j2+"\n");
            sb.append("MB;"+m/length+";"+j1+"_"+j2+"\n");
            sb.append("UB;"+u/length+";"+j1+"_"+j2+"\n");
            sb.append("SB;"+(a+d+m+u)/length+";"+j1+"_"+j2+"\n");


//        log.info("------------------------ no filter ---------------------");
//        log.info("A: "+ a);
//        log.info("D: "+ d);
//        log.info("M: "+ m);
//        log.info("U: "+ u);
//        log.info("SUM: " + (a+d+m+u));
            log.info("Started with " +j1+"_"+j2+" filter");
            tree1 = travisService.getBuildLogTree(j1,true);
            tree2 = travisService.getBuildLogTree(j2,true);
            editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
            a = EditTreeUtils.getAdditions(editTree);
            d = EditTreeUtils.getDeletions(editTree);
            m = EditTreeUtils.getMoves(editTree);
            u = EditTreeUtils.getUpdates(editTree);
//        log.info("------------------------ filter ---------------------");
//        log.info("A: "+ a);
//        log.info("D: "+ d);
//        log.info("M: "+ m);
//        log.info("U: "+ u);
//        log.info("SUM: " + (a+d+m+u));
            sb.append("AA;"+a/length+";"+j1+"_"+j2+"\n");
            sb.append("DA;"+d/length+";"+j1+"_"+j2+"\n");
            sb.append("MA;"+m/length+";"+j1+"_"+j2+"\n");
            sb.append("UA;"+u/length+";"+j1+"_"+j2+"\n");
            sb.append("SA;"+(a+d+m+u)/length+";"+j1+"_"+j2+"\n");

        }

        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\r\\", "verifyProcessing.txt", sb.toString());

    }

    @RequestMapping("/analysis/verifyThreshold")
    public void verifyThreshold(){
        String[][] jobs = {
                {"555684793", "555819512"}, // noahch/blogdiff
                {"549574901", "549928009"}, // noahch/blogdiff
                {"559110346", "559670786"}, // apache/accumulo
                {"563306214", "563328513"}, // apache/accumulo
                {"558000323", "559134087"}, // asciidocfx/AsciidocFX
                {"443199899", "447124533"}, // asciidocfx/AsciidocFX
                {"535133332", "535138722"}, // simpligility/android-maven-plugin
                {"530123093", "530123093"}, // simpligility/android-maven-plugin
                {"432465252", "435620282"}, // spotify/docker-client
                {"460130349", "460137537"}, // spotify/docker-client
                {"496369595", "499906077"}, // spring-projects/spring-data-elasticsearch
                {"407839576", "407842660"}, // wstrange/GoogleAuth
                {"515753486", "515767196"}, // wstrange/GoogleAuth
                {"556112100", "557479328"}, // google/dagger
                {"421217921", "421231392"}, // swagger-api/swagger-core
                {"543128552", "543716155"}, // apache/struts
                {"499807007", "499816460"}, // spring-projects/spring-data-jpa
                {"339309648", "375182522"}, // Nukkit/Nukkit
                {"380991952", "382538251"}, // Nukkit/Nukkit
                {"529851442", "532916599"}, // google/compile-testing
                {"445185673", "445783150"}, // tumblr/jumblr
                {"445069535", "445069535"}, // tumblr/jumblr
                {"545469486", "547020095"}, // google/auto
                {"544534517", "545469486"},  // google/auto
                {"559927762","559940341"}, // zxing/zxing
                {"546401404","546407833"}, // zxing/zxing
                {"559362399","560338115"}, // jhy/jsoup
                {"554472010","554508997"}, // jhy/jsoup
                {"542459765","542536960"}, // twitter/distributedlog
                {"564966733","564969707"}, // apache/zookeper
                {"564866502","564896569"}, // apache/zookeper
                {"525327953","525322578"},
                {"564752286","564751025"},
                {"564861420","564846548"},
                {"564910577","564867089"},
                {"564082693","563724891"},
                {"564140899","564102794"},
                {"564998357","564942136"},
                {"563464587","562890235"},
                {"564924765","564913170"},
                {"564141017","564140951"},
                {"563063238","562439986"},
                {"564868958","564858893"},
                {"564968915","564933604"},
                {"460137537","460130349"},
                {"564958360","563179305"},
                {"459136146","458926403"},
                {"563838048","563781605"},
                {"561122106","561119997"},
                {"163676138","163675377"},
                {"556166624","554719922"},
                {"542754905","542754667"},
                {"564352242","563440873"},
                {"564994546","564910919"},
                {"564141871","564132039"},
                {"561677800","560791887"},
                {"555040848","554733157"},
                {"561183076","561103678"},
                {"562925498","562427271"},
                {"564812364","564547448"},
                {"543059026","541365362"},
                {"564993969","564802345"},
                {"541342682","539685861"},
                {"563953733","563927025"},
                {"540095970","535138722"},
                {"559558395","559546588"},
                {"562915402","562627374"},
                {"562666728","560902560"},
                {"542533310","541986813"},
                {"542895760","542890482"},
                {"547020095","546963913"},
                {"403399589","403374157"},
                {"560241572","560231909"},
                {"542479699","541157731"},
                {"564686003","564439410"},
                {"563880944","563591174"},
                {"560856449","560788294"},
                {"564698401","564685557"},
                {"509516393","507220804"},
                {"327017018","327015017"},
                {"560927877","557923739"},
                {"231887132","223274350"},
                {"564821552","564821425"},
                {"563812726","563810976"},
                {"267359118","267355354"},
                {"542545806","542388610"},
                {"563622048","563591271"},
                {"467860405","467841525"},
                {"562629358","562627156"},
                {"564264670","564249086"},
                {"562790570","562785217"},
                {"564433600","564394678"},
                {"563557613","563539890"},
        };
        double[] thresholds = {0.5,0.4,0.3,0.2,0.1};
        StringBuilder sb = new StringBuilder();
        sb.append("Threshold;A;D;U;M;Total\n");
        for(int i = 0; i < jobs.length; i++) {

            for(int t = 0; t < thresholds.length; t++){
                String j1 = jobs[i][0];
                String j2 = jobs[i][1];
                log.info("Started:" + i);
                BuildLogTree tree1 = travisService.getBuildLogTree(j1,true);
                BuildLogTree tree2 = travisService.getBuildLogTree(j2,true);
                EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer(thresholds[t]));
                double a = EditTreeUtils.getAdditions(editTree);
                double d = EditTreeUtils.getDeletions(editTree);
                double m = EditTreeUtils.getMoves(editTree);
                double u = EditTreeUtils.getUpdates(editTree);
                sb.append(thresholds[t]+";"+a+";"+d+";"+u+";"+m+";"+(a+m+d+u)+"\n");

            }

        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "nm_thresholdsBIG.txt", sb.toString());


    }

    @RequestMapping("/analysis/series")
    public void verifySeries(){
        String[][] jobs = {
                {"564752286","564751025","564241321","563781320","563313312","563307817"},
                {"564910577","564867089","564822947","564822944","564822543","564822533"},
                {"564082693","563724891","563677552","563606477","563600705","563556986"},
                {"565040684","564140899","564102794","564099441","563868471","563868249"},
                {"565079297","565013673","564998357","564942136","564885714","564486434"},
                {"565115157","565109216","564480669","564423493","564413579","564378114"},
                {"563464587","562890235","562846367","562846324","562840640","557682272"},
                {"564924765","564913170","564856514","564127477","564125816","564124259"},
                {"564141017","564140959","564140951","564134362","560091601","557978281"},
                {"563063238","562439986","562425890","562037173","561934881","561933546"},
                {"564868958","564858893","564855137","564836429","564782997","563896253"},
                {"565056170","565022667","564968915","564933604","564883378","564873656"},
                {"460137537","460130349","457393947","453979855","453747457","453675498"},
                {"565122430","565096604","565096598","564958360","563179305","563179299"},
                {"563838048","563781605","563634983","563565721","563560257","563343289"},
                {"565040003","565022116","565013206","565001616","564324464","564174292"},
                {"562938065","561122106","561119997","556877243","556730152","556693633"},
                {"163676138","163675377","162779689","162777740","161902649","161900456"},
                {"564352242","563440873","559317483","558217226","558215093","557818150"},
                {"564994546","564964240","564945416","564931307","564926194","564923042"},
                {"564141871","564132039","555946207","549251996","543966262","540987921"},
                {"561183076","561183040","561131197","561103982","561103922","561103805"},
                {"562925498","562427271","561938768","561602794","561319023","560854656"},
                {"564812364","564547448","564294493","563857538","563393758","562910919"},
                {"565116348","565112436","565111515","565109350","565107768","565106758"},
                {"563953733","563953729","563951460","563927025","563922754","563919057"},
                {"540095970","535138722","535133332","535132700","530123093","530117440"},
                {"559558395","559546588","558202965","557955368","557932674","557479292"},
                {"562915402","562627374","560171807","559627096","557957381","555859359"},
                {"562666728","560902560","555634329","555626655","555624447","555623301"},
                {"547020095","546963913","545469486","544534517","542318599","542282987"},
                {"542479699","541157731","538292233","538291333","536702098","536054251"},
                {"565103730","564686003","564439410","564129906","563674866","563202713"},
                {"563880944","563591174","563588911","563061004","562941921","562794652"},
                {"560856449","560788294","556322675","547542186","546309872","546283114"},
                {"564698401","564698339","564685557","562152500","562151368","562145324"},
                {"231887132","223274350","223131349","222049024","222048771","221821695"},
                {"564821552","564821543","564821425","564821418","564821273","564821260"},
                {"563812726","563810976","563809429","562643035","557458007","557433550"},
                {"563622048","563591271","563591249","559609240","557036784","557028865"},
                {"562629358","562627156","562627143","562625189","562625154","562623523"},
                {"564264670","564249696","564249086","564244493","563865925","563865153"},
                {"562790570","562785217","562516334","562499511","562409300","562405812"},
                {"564433600","564394720","564394678","564359717","564358816","564062471"},
                {"563557613","563539890","563465151","562683680","562554710","562541020"},
        };

        StringBuilder sb = new StringBuilder();
        sb.append("Run;Dist;A;D;U;M;Total;Len\n");

        for(int i = 0; i < jobs.length; i++){
            for(int j = 1; j < jobs[i].length; j++){
                try {
                    String j1 = jobs[i][0];
                    String j2 = jobs[i][j];
                    log.info("Started:" + i);
                    double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
                    double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
                    double length = max(l1, l2);
                    BuildLogTree tree1 = travisService.getBuildLogTree(j2, true);
                    BuildLogTree tree2 = travisService.getBuildLogTree(j1, true);
                    EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
                    double a = EditTreeUtils.getAdditions(editTree);
                    double d = EditTreeUtils.getDeletions(editTree);
                    double m = EditTreeUtils.getMoves(editTree);
                    double u = EditTreeUtils.getUpdates(editTree);
                    sb.append(i + ";" + j + ";" + a + ";" + d + ";" + u + ";" + m + ";" + (a + m + d + u) + ";" + length + "\n");
                }catch (Exception e){
                    log.info("oops");
                }
            }
        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "seriesBIG.txt", sb.toString());


        sb = new StringBuilder();
        sb.append("Run;Dist;A;D;U;M;Total;Len\n");

        for(int i = 0; i < jobs.length; i++){
            for(int j = 1; j < jobs[i].length; j++){
                try {
                    String j1 = jobs[i][j-1];
                    String j2 = jobs[i][j];
                    log.info("Started:" + i);
                    double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
                    double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
                    double length = max(l1,l2);
                    BuildLogTree tree1 = travisService.getBuildLogTree(j2,true);
                    BuildLogTree tree2 = travisService.getBuildLogTree(j1,true);
                    EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
                    double a = EditTreeUtils.getAdditions(editTree);
                    double d = EditTreeUtils.getDeletions(editTree);
                    double m = EditTreeUtils.getMoves(editTree);
                    double u = EditTreeUtils.getUpdates(editTree);
                    sb.append(i+";"+j+";"+a+";"+d+";"+u+";"+m+";"+(a+m+d+u)+";"+length+"\n");
                }catch (Exception e){
                    log.info("oops");
                }
            }
        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "seriesSucBIG.txt", sb.toString());
    }

    public static double max(double a, double b){
        if(a < b){
            return b;
        }
        return a;
    }
}
