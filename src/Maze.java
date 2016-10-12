import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Maze {
    private int n;                 // dimension of maze
    private boolean[][] north;     // is there a wall to north of cell i, j
    private boolean[][] east;
    private boolean[][] south;
    private boolean[][] west;
    private boolean[][] visited;
    private boolean[][] initialVisited;
    private boolean done = false;
    private int passos = 0;
    private List<int[]> listaAberta = new ArrayList<>();
    private List<int[]> caminho = new ArrayList<>();
    private int posBonusX;
    private int posBonusY;
    private int posTrapX;
    private int posTrapY;
    private int extra = 0;

    public Maze(int n) {
        this.n = n;
        StdDraw.setXscale(0, n+2);
        StdDraw.setYscale(0, n+2);
        init();
        generate();
        this.initialVisited = this.visited.clone();
    }
    
    private void generateTrap() {
    	Random generator = new Random();
    	posBonusX = generator.nextInt(this.n);
    	posBonusY = generator.nextInt(this.n);
    	StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledCircle(posBonusX + 0.5, posBonusY + 0.5, 0.25);
	}

	private void generateBonus() {
		Random generator = new Random();
		posTrapX = generator.nextInt(this.n);
		posTrapY = generator.nextInt(this.n);
		StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledCircle(posTrapX + 0.5, posTrapY + 0.5, 0.25);
	}
	
	private void checkForBonus(int x, int y){
		if(posBonusX == x && posBonusY == y){
			extra += 10;
		}
	}
	
	private void checkForTrap(int x, int y){
		if(posTrapX == x && posTrapY == y){
			extra -= 10;
		}
	}

	public boolean[][] getEast() {
		return east;
	}
    
    public boolean[][] getNorth() {
		return north;
	}
    
    public boolean[][] getSouth() {
		return south;
	}
    
    public boolean[][] getWest() {
		return west;
	}
    
    private void init() {
        // initialize border cells as already visited
        visited = new boolean[n+2][n+2];
        for (int x = 0; x < n+2; x++) {
            visited[x][0] = true;
            visited[x][n+1] = true;
        }
        for (int y = 0; y < n+2; y++) {
            visited[0][y] = true;
            visited[n+1][y] = true;
        }


        // initialze all walls as present
        north = new boolean[n+2][n+2];
        east  = new boolean[n+2][n+2];
        south = new boolean[n+2][n+2];
        west  = new boolean[n+2][n+2];
        for (int x = 0; x < n+2; x++) {
            for (int y = 0; y < n+2; y++) {
                north[x][y] = true;
                east[x][y]  = true;
                south[x][y] = true;
                west[x][y]  = true;
            }
        }
    }


    // generate the maze
    private void generate(int x, int y) {
        visited[x][y] = true;

        // while there is an unvisited neighbor
        while (!visited[x][y+1] || !visited[x+1][y] || !visited[x][y-1] || !visited[x-1][y]) {

            // pick random neighbor (could use Knuth's trick instead)
            while (true) {
                double r = StdRandom.uniform(4);
                if (r == 0 && !visited[x][y+1]) {
                    north[x][y] = false;
                    south[x][y+1] = false;
                    generate(x, y + 1);
                    break;
                }
                else if (r == 1 && !visited[x+1][y]) {
                    east[x][y] = false;
                    west[x+1][y] = false;
                    generate(x+1, y);
                    break;
                }
                else if (r == 2 && !visited[x][y-1]) {
                    south[x][y] = false;
                    north[x][y-1] = false;
                    generate(x, y-1);
                    break;
                }
                else if (r == 3 && !visited[x-1][y]) {
                    west[x][y] = false;
                    east[x-1][y] = false;
                    generate(x-1, y);
                    break;
                }
            }
        }
    }

    // generate the maze starting from lower left
    private void generate() {
        generate(1, 1);


        // delete some random walls
        for (int i = 0; i < 1500; i++) {
            int x = 1 + StdRandom.uniform(n-1);
            int y = 1 + StdRandom.uniform(n-1);
            north[x][y] = south[x][y+1] = false;
        }
/*
        // add some random walls
        for (int i = 0; i < 10; i++) {
            int x = n/2 + StdRandom.uniform(n/2);
            int y = n/2 + StdRandom.uniform(n/2);
            east[x][y] = west[x+1][y] = true;
        }
*/
     
    }

    public void clear() {
    	this.visited = this.initialVisited.clone();
    	this.passos = 0;
    	this.extra = 0;
    }

    // solve the maze using depth-first search
    private void solve(int x, int y) {
        if (x == 0 || y == 0 || x == n+1 || y == n+1) return;
        if (done || visited[x][y]) return;
        visited[x][y] = true;
        passos++;
        checkForBonus(x, y);
        checkForTrap(x, y);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(50);

        // reached middle
        if (x == n && y == n) done = true;

        if (!north[x][y]) solve(x, y + 1);
        if (!east[x][y])  solve(x + 1, y);
        if (!south[x][y]) solve(x, y - 1);
        if (!west[x][y])  solve(x - 1, y);

        if (done) return;
        passos++;
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(30);
    }

    private void solveLista(int x, int y) {
        visited[x][y] = true;
        passos++;
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(30);
        int atual[] = {x, y};
        caminho.add(atual);	
        // reached middle
        if (x == n && y == n) return;
        if (!north[x][y] && !visited[x][y+1]) {
        	int posicaoN[] = new int[2];
        	posicaoN[1] = y + 1;
        	posicaoN[0] = x;	
        	listaAberta.add(posicaoN);
        }
        if (!east[x][y] && !visited[x+1][y])  {
        	int posicaoE[] = new int[2];
        	posicaoE[1] = y;
        	posicaoE[0] = x + 1;	
        	listaAberta.add(posicaoE);
        }
        if (!south[x][y] && !visited[x][y-1]) {
        	int posicaoS[] = new int[2];
        	posicaoS[1] = y - 1;
        	posicaoS[0] = x;	
        	listaAberta.add(posicaoS);
        }
        if (!west[x][y] && !visited[x-1][y])  {
        	int posicaoW[] = new int[2];
        	posicaoW[1] = y;
        	posicaoW[0] = x - 1;	
        	listaAberta.add(posicaoW);
        }
        int maiorPonto[] = getMaiorPonto();
        if (isPontoVizinho(maiorPonto, x, y)) {
        	listaAberta.remove(maiorPonto);
        	solveLista(maiorPonto[0], maiorPonto[1]);
        } else {
        	returnToPoint(maiorPonto, atual);
        }
    }
    
    private void returnToPoint(int[] ponto, int[] atual) {
    	int i = caminho.indexOf(atual) - 1;
    	for (; i>0;i--) {
    		int anterior[] = caminho.get(i);
			passos++;
	        caminho.remove(anterior);
	        if (isPontoVizinho(ponto, anterior[0], anterior[1])) {
	        	solveLista(anterior[0], anterior[1]);
	        	break;
	        }
    	}
    }
    
    private boolean isPontoVizinho(int[] ponto, int x, int y) {
    	return ponto[0] == x + 1
    			|| ponto[0] == x - 1
    			|| ponto[1] == y + 1
    			|| ponto[1] == y - 1;
    }
    
    private int[] getMaiorPonto() {
    	int ponto[] = new int[2];
    	for (int[] item: listaAberta) {
    		if (item[0] + item[1] >= ponto[0] + ponto[1]) {
    			ponto = item;
    		}
    	}
    	return ponto;
    }
    
    private void solveWithHill(int x, int y) {
    	if (x == 0 || y == 0 || x == n+1 || y == n+1) return;
        if (done || visited[x][y]) return;
        visited[x][y] = true;
        passos++;
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(50);
        // reached middle
        if (x == n && y == n) done = true;
        
        int norte = Integer.MIN_VALUE;
        int sul = Integer.MIN_VALUE;
        int leste = Integer.MIN_VALUE;
        int oeste = Integer.MIN_VALUE;
        if (!north[x][y]) {
        	norte = 1;
        	norte += n - y;
        };
        if (!east[x][y]) {
        	leste = 1;
        	leste += n - x;
        };
        if (!south[x][y]) {
        	sul = -1;
        	sul += y - n;
        };
        if (!west[x][y]) {
        	oeste = -1;
        	oeste += x - n;
        };
        int lista[] = {norte, sul, leste, oeste};
        Arrays.sort(lista);
        for (int i=lista.length; i>0; i--) {
        	int num = lista[i-1];
        	if (num != Integer.MIN_VALUE) {
	        	if (num == norte) solveWithHill(x, y + 1);
	            if (num == leste)  solveWithHill(x + 1, y);
	            if (num == sul) solveWithHill(x, y - 1);
	            if (num == oeste)  solveWithHill(x - 1, y);
        	}
        }
        
        if (done) return;
        passos++;
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(30);
    }
    
    // solve the maze starting from the start state
    public void solve(int i) {
        for (int x = 1; x <= n; x++)
            for (int y = 1; y <= n; y++)
                visited[x][y] = false;
        done = false;
        if (i == 0) {
        	solveWithHill(1, 1);
        } else if (i == 1) {
        	solve(1, 1);
        } else if (i == 2) {
        	solveLista(1, 1);
        }
    }

    // draw the maze
    public void draw() {
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(n + 0.5, n + 0.5, 0.375);
        StdDraw.filledCircle(1.5, 1.5, 0.375);

        StdDraw.setPenColor(StdDraw.BLACK);
        for (int x = 1; x <= n; x++) {
            for (int y = 1; y <= n; y++) {
                if (south[x][y]) StdDraw.line(x, y, x+1, y);
                if (north[x][y]) StdDraw.line(x, y+1, x+1, y+1);
                if (west[x][y])  StdDraw.line(x, y, x, y+1);
                if (east[x][y])  StdDraw.line(x+1, y, x+1, y+1);
            }
        }
        StdDraw.show();
        StdDraw.pause(1000);
    }
    
    public int getPassos() {
		return passos;
	}

    // a test client
    public static void main(String[] args) {
        int n = 40;
        Maze maze = new Maze(n);
        StdDraw.enableDoubleBuffering();
        maze.draw();
        maze.generateBonus();
        maze.generateTrap();
        StdDraw.pause(1000);
        maze.solve(1);
        System.out.println(maze.getPassos());
        System.out.println(maze.extra);
        StdDraw.clear();
        StdDraw.show();
        maze.draw();
        maze.clear();
        maze.generateBonus();
        maze.generateTrap();
        maze.solve(0);
        System.out.println(maze.getPassos());
        System.out.println(maze.extra);
        StdDraw.clear();
        StdDraw.show();
        maze.draw();
        maze.clear();
        maze.generateBonus();
        maze.generateTrap();
        maze.solve(2);
        System.out.println(maze.getPassos());
        System.out.println(maze.extra);
    }

}