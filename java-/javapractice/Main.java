import java.util.Scanner;



public class Main {

   



        static class Player{

            private int id;

            private String role;

            private boolean alive;

            //private id,role,alive

        }



        public Player(){

            this.id = id;

            this.role = role;

            this.alive = true;            

        }



        public int getId(){

            return id;

        }



        public String getRole(){

            return role;

        }



        public boolean isAlive(){

            return alive;

        }



        public void kill(){

            alive = false;

        }

        public String getPublicInfo(){

            if(alive){

                return "Player " + id + "[Alive]";

            }else{

                return "Player " + id + "[Dead]";

            }

        }

        public static void main (String[] args){

            Scanner sc = new Scanner(System.in)

            Random rand = new Random();

            System.out.println("wolfGame");

            System.out.println("Enter number of players(4~10)");

            int n = sc.nextInt();

            sc.nextInt();

            while (n < 4 || n > 10) {

                System.out.println("4~10");

                n = sc.nextInt();

                sc.nextLine();

            }

            Player[i] players = new Player[n];

            int wolfIndx = rand.nextInt();

            for (int i = 0 ; i < n; i++){

                if( i == wolfIndex){

                    players[i] = new Player(i+1,role = "Werewolf");

                }else{

                    players[i] =

                }

            }

            System.out.println();        

            System.out.print("Role assignment start.")

            System.out.print("Each player take turn");

            for(int i = 0 ; i < n ; i++){

                System.err.println();

                System.out.println("Player",+(i+1)) , "Please Enter.";

                sc,nextLine();

                System.out.println("Your Role :" + players[i].getRole());

                System.out.println("Memorize your role , then turn.");

                sc.nextLine();

                for(int line = 0; line < 30; line++){

                    System.out.println();

                }

            }

            boolean gameOver = false;

            int round = 1;

            while (!gameOver) {

                System.out.println("Round", + round);

                System.out.println();



                System.out.println("Night falls. Werewolf wakes up.");

                int aliveWolf = findAliveWolf(players);

                if (aliveWereWolf != -1){

                    System.out.println("Werewolf is your turn.");

                    System.out.println("Alive players ");

                    printAlivePlayers(players);



                    int target = -1;



                    while(true){

                        System.out.println("choose a player to kill");

                        if(sc.hasNext()){

                            targetId = sc.nextInt();

                            System.out.println();

                            if(isValidTarget(targetId, players[aliveWereWolf].getId())) {

                                break;

                            }else{

                                System.out.println("Invalid target.please again");

                                sc.nextLine();

                            }

                        }

                    }

                    players[targetId -1].kill();

                    System.out.println("Night results: Player"+targetId+"has been killed.");

                    else{

                        System.out.println("no werewolf alive. Skipping night pha")

                    }

                    if(checkKilllargerWin(players)){

                        gameOver = true;

                        break;

                    }else if (checkKillwerewolfwin(players)) {

                        System.out.println();

                        System.out.println("Werewolf wins! All villagers has been killed.");

                        gameOver = true;

                    }

                    if (gameOver){

                        break;

                    }

                    int voteId = -1;



                    Public static void printAlivePlayers(player[]players){

                        for(int i = 0;i < players.length;i++){

                            if(players[i].isAlive()&&players[i].getRole()equals("Werewolf")){

                                trturn i;

                            }

                        }

                        return -1;

                    }

                    public static printAlivePlayers(player[]players){

                        for(int i = 0;i < players.length;i++){

                            System.out.print(players[i].getPublicInfo());

                        }



                    }

                       

                   

                }

            }

           

           

        }





   

}
