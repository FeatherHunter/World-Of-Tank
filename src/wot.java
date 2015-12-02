import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
public class wot extends JFrame{

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WotJFrame wotJFrame = new WotJFrame();
		//this.add
		//while(true);

	}

}
class WotJFrame extends JFrame{
	TankPanel tPanel = null;
	public WotJFrame()
	{
		tPanel = new TankPanel();
		this.add(tPanel);
		this.addKeyListener(tPanel);
		Thread thread = new Thread(tPanel);
		thread.start();
		this.setSize(1000,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	
}

class Bomb implements Runnable{
	int x;
	int y;
	int life;
	Image boomImage = null; 
	boolean isLive;
	public Bomb(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.life = 1000000;
		isLive = true;
		boomImage = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/boom3.gif"));
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isLive = false;
	}
}

class TankPanel extends JPanel implements KeyListener,Runnable{
	HeroTank hero = new HeroTank("IS3", 300, 300, 3, 0);
	Vector<EnemyTank> enemysVector = null;
	Bullet bullet = null;
	int etNum = 3;
	int x = 0, y = 0;
	int xPoints[];
	int yPoints[];
	int tankX;
	int tankY;

	Vector<Bomb> bombsVector = null;
	
	public TankPanel()
	{
		this.setLayout(null);
		xPoints = new int[100];
		yPoints = new int[100];

		enemysVector = new Vector<EnemyTank>();
		bombsVector = new Vector<Bomb>();
		for(int i = 0; i < etNum; i++)
		{
			EnemyTank eTank = new EnemyTank("IS3",100 * i + 50, 100, 3, 1);
			Thread thread = new Thread(eTank);
			thread.start();
			enemysVector.add(eTank);
		}
	}
	public void paint(Graphics gra)
	{
		super.paint(gra);
		if(hero.x < 40) hero.x = 40;
		else if(hero.x > 500) hero.x = 500;
		if(hero.y < 60) hero.y = 60;
		else if(hero.y > 500) hero.y = 500;
		drawTank(hero, gra);
		/*爆炸效果*/
		for(int i = 0; i < bombsVector.size(); i++)
		{
			Bomb bomb = bombsVector.get(i);
			if(bomb.isLive)
			{
				gra.drawImage(bomb.boomImage, bomb.x - 50, bomb.y - 50, 100, 100 ,this);
			}
			else if(bomb.isLive == false) {
					bombsVector.remove(bomb);
			}

		}
		/*敌人坦克*/
		for(int i = 0; i < enemysVector.size(); i++)
		{
			/*不能越过边界*/
			EnemyTank eTank = enemysVector.get(i);
			if(eTank.x < 40) eTank.x = 40;
			else if(eTank.x > 500) eTank.x = 500;
			if(eTank.y < 60) eTank.y = 60;
			else if(eTank.y > 500) eTank.y = 500;
			/*敌人间不碰撞*/
			for(int j = 0; j < enemysVector.size(); j++)
			{
				if(j == i) continue;
				collision(eTank, enemysVector.get(j));//解决碰撞			
			}
			
			int num = (int)Math.random()/2;
			switch (num) {
			case 0:
				if(eTank.bullets.size() <= eTank.bulletMax)
				{
					eTank.shoot();
				}
				break;
			}
			for(int j = 0; j < eTank.bullets.size(); j++)
			{
				bullet = eTank.bullets.get(i);
				checkHit(bullet, hero);
				if(hero.isLive == false)
				{
					Bomb bomb = new Bomb(hero.x, hero.y); 
					Thread thread = new Thread(bomb);
					thread.start();
					bombsVector.add(bomb);
					gra.setFont(new Font("华文彩云",Font.BOLD, 30));
					gra.setColor(Color.RED);
					gra.drawString("GAME OVER!", 200, 200);
					
				}
				if((bullet != null) && bullet.isLive)
				{
					gra.setColor(Color.BLACK);
					gra.fillOval(bullet.x, bullet.y, 4, 4);
				}
				if(bullet.isLive == false)
				{
					eTank.bullets.remove(bullet);
				}
			}
			drawTank(eTank, gra);
		}
		/*本人的子弹*/
		for(int i = 0; i < hero.bullets.size(); i++)
		{
			bullet = hero.bullets.get(i);
			for(int j = 0; j < enemysVector.size(); j++)
			{
				Tank tank = enemysVector.get(j);
				//bombsVector.add(new Bomb(tank.x, tank.y));
				checkHit(bullet, tank);
				if(tank.isLive == false)
				{
					Bomb bomb = new Bomb(tank.x, tank.y); 
					Thread thread = new Thread(bomb);
					thread.start();
					bombsVector.add(bomb);
					System.out.println("check hit");
					enemysVector.remove(tank);
				}
			}
			if((bullet != null) && bullet.isLive)
			{
				gra.setColor(Color.BLACK);
				gra.fillOval(bullet.x, bullet.y, 4, 4);
			}
			if(bullet.isLive == false)
			{
				hero.bullets.remove(bullet);
			}
		}
	}
	public void collision(Tank aTank, Tank bTank)
	{
		int tempx, tempy;
		int otherXMax, otherYMax;
		int otherXMin, otherYMin;
		switch (aTank.direction) {
		/*Up*/
		case 0:
		{
			switch (bTank.direction) {
				case 0:
					/*均朝上*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 26;
					otherYMax = bTank.y + 44;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
			
					break;
				case 1:
					/*上下*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 44;
					otherYMax = bTank.y + 26;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 2:
					/*上左*/
					otherXMin = bTank.x - 26;
					otherXMax = bTank.x + 44;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 3:
					/*上右*/
					otherXMin = bTank.x - 44;
					otherXMax = bTank.x + 26;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
			}//end of switch
		
		}
		break;
		/*Down*/
		case 1:
		{
			switch (bTank.direction) {
				case 0:
					/*下上*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 26;
					otherYMax = bTank.y + 44;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
			
					break;
				case 1:
					/*下下*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 44;
					otherYMax = bTank.y + 26;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 2:
					/*下左*/
					otherXMin = bTank.x - 26;
					otherXMax = bTank.x + 44;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 3:
					/*下右*/
					otherXMin = bTank.x - 44;
					otherXMax = bTank.x + 26;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 20;
					tempy = aTank.y - 44;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 20;
					tempy = aTank.y + 26;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					
					break;
			}//end of switch
		
		}
		break;
		/*Left*/
		case 2:
		{
			switch (bTank.direction) {
				case 0:
					/*左上*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 26;
					otherYMax = bTank.y + 44;
					/*左上角*/
					tempx = aTank.x - 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
			
					break;
				case 1:
					/*左下*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 44;
					otherYMax = bTank.y + 26;
					/*左上角*/
					tempx = aTank.x - 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 2:
					/*左左*/
					otherXMin = bTank.x - 26;
					otherXMax = bTank.x + 44;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 3:
					/*左右*/
					otherXMin = bTank.x - 44;
					otherXMax = bTank.x + 26;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					
					break;
			}//end of switch
		
		}
		break;
		/*Right*/
		case 3:
		{
			switch (bTank.direction) {
				case 0:
					/*右上*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 26;
					otherYMax = bTank.y + 44;
					/*左上角*/
					tempx = aTank.x - 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
			
					break;
				case 1:
					/*右下*/
					otherXMin = bTank.x - 20;
					otherXMax = bTank.x + 20;
					otherYMin = bTank.y - 44;
					otherYMax = bTank.y + 26;
					/*左上角*/
					tempx = aTank.x - 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 2:
					/*右左*/
					otherXMin = bTank.x - 26;
					otherXMax = bTank.x + 44;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					break;
				case 3:
					/*右右*/
					otherXMin = bTank.x - 44;
					otherXMax = bTank.x + 26;
					otherYMin = bTank.y - 20;
					otherYMax = bTank.y + 20;
					/*左上角*/
					tempx = aTank.x - 44;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y += otherYMax - tempy;
					}
					/*右上角*/
					tempx = aTank.x + 26;
					tempy = aTank.y - 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y += otherYMax - tempy;
					}
					/*左下角*/
					tempx = aTank.x - 44;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x += otherXMax - tempx;
						aTank.y -= tempy - otherYMin;
					}
					/*右下角*/
					tempx = aTank.x + 26;
					tempy = aTank.y + 20;
					if((tempx  > otherXMin)&&(tempx < otherXMax)&&(tempy > otherYMin)&&(tempy < otherYMax))
					{
						aTank.x -= tempx - otherXMin;
						aTank.y -= tempy - otherYMin;
					}
					
					break;
			}//end of switch
		
		}
		break;
		

		}//end of switch
		
	}
	public void drawTank(Tank tk, Graphics gra)
	{
		tankX = tk.getX();
		tankY = tk.getY();

		if(tk.getTankName().equals("IS3")&&(tk.isLive==true))
		{
			if(tk.getDirection() == 1)
			{
				tankX -= 21;
				tankY -= 60;
				Image img = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/IS3_210_600_down.png"));
				gra.drawImage(img, tankX, tankY, 42, 120, this);
			}
			else if(tk.getDirection() == 0)
			{
				tankX -= 21;
				tankY -= 60;
				tankY -= 40;
				Image img = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/IS3_210_600_up.png"));
				gra.drawImage(img, tankX, tankY, 42, 120, this);
			}
			else if(tk.getDirection() == 2)
			{
				tankX -= 80;
				tankY -= 42;
				Image img = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/IS3_600_210_left.png"));
				gra.drawImage(img, tankX, tankY, 120, 42, this);
			}
			else if(tk.getDirection() == 3)
			{
				tankX -= 80;
				tankX += 40;
				tankY -= 42;
				Image img = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/IS3_600_210_right.png"));
				gra.drawImage(img, tankX, tankY, 120, 42, this);
			}
			
		}		
		else if(tk.getTankName().equals("Jagdtiger")&&(tk.isLive==true))
		{
			if(tk.getDirection() == 0)
			{
				gra.setColor(Color.LIGHT_GRAY);
				
				gra.fill3DRect(tankX-15, tankY-15, 30, 30, false);//1:中
				gra.fill3DRect(tankX-15-5, tankY-15-20, 5, 20, false);//2
				gra.fill3DRect(tankX+15, tankY-15-20, 5, 20, false);//3
				gra.fill3DRect(tankX-15-5, tankY+15, 5, 20, false);//4
				gra.fill3DRect(tankX+15, tankY+15, 5, 20, false);//5
				gra.fill3DRect(tankX-15-5, tankY-15, 5, 30, false);//6
				gra.fill3DRect(tankX+15, tankY-15, 5, 30, false);//7
				
				gra.fill3DRect(tankX-15, tankY-15-18, 30, 8, false);//8
				gra.fill3DRect(tankX-15, tankY-15-10, 30, 10, false);//9
				gra.setColor(Color.DARK_GRAY);
				gra.fill3DRect(tankX-15+2, tankY-15-10+2, 2, 8, false);//10 in 9
				gra.setColor(Color.LIGHT_GRAY);
				gra.fill3DRect(tankX-15, tankY+15, 30, 20, false);//11 trail
				/*装饰品*/
				gra.fill3DRect(tankX-15, tankY+15+12, 10, 6, true);//12
				gra.fill3DRect(tankX+5, tankY+15+12, 10, 6, true);//13
				gra.setColor(Color.DARK_GRAY);
				gra.drawLine(tankX-14, tankY+27, tankX-14, tankY+27+6);
				gra.drawLine(tankX-12, tankY+27, tankX-12, tankY+27+6);
				gra.drawLine(tankX-10, tankY+27, tankX-10, tankY+27+6);
				gra.drawLine(tankX-8,  tankY+27, tankX-8, tankY+27+6);
				
				gra.drawLine(tankX+6, tankY+27, tankX+6, tankY+27+6);
				gra.drawLine(tankX+8, tankY+27, tankX+8, tankY+27+6);
				gra.drawLine(tankX+10, tankY+27, tankX+10, tankY+27+6);
				gra.drawLine(tankX+12,  tankY+27, tankX+12, tankY+27+6);
				
				gra.setColor(Color.DARK_GRAY);
				gra.fillRect(tankX-11, tankY+34, 5, 2);//14
				gra.fillRect(tankX+6, tankY+34, 5, 2);//15
				gra.fillOval(tankX + 3, tankY + 16, 10, 10);//16
				gra.fillOval(tankX - 2, tankY + 25, 4, 4);//17
				gra.setColor(Color.LIGHT_GRAY);
				gra.fillOval(tankX + 5, tankY + 18, 6, 6);//16

				//炮塔
				gra.setColor(Color.GRAY);
					
				gra.fill3DRect(tankX-15, tankY-17, 29, 33, true);//1:w外
				gra.fill3DRect(tankX-10, tankY-15, 19, 29, true);//2:内
				gra.setColor(Color.DARK_GRAY);
				gra.drawLine(tankX-15,  tankY-17, tankX-10, tankY-15);
				gra.drawLine(tankX+14,  tankY-17, tankX+9, tankY-15);
				gra.drawLine(tankX-14,  tankY+16, tankX-9, tankY+14);
				gra.drawLine(tankX+14,  tankY+16, tankX+9, tankY+14);


				tankY -= 15;
					
				gra.setColor(Color.GRAY);
					
				gra.fill3DRect(tankX-3, tankY-12, 6, 5, true);//1:zhong
				gra.fill3DRect(tankX-2, tankY-37, 4, 25, true);//2:内
				xPoints[0] = tankX-3;
				yPoints[0] = tankY-6;
				xPoints[1] = tankX+3;
				yPoints[1] = tankY-6;
				xPoints[2] = tankX+6;
				yPoints[2] = tankY;
				xPoints[3] = tankX-6;
				yPoints[3] = tankY;
				gra.setColor(Color.DARK_GRAY);
				gra.fillPolygon(xPoints, yPoints, 4);
			
			}//end of 'w'
			else if(tk.getDirection() == 1)
			{
gra.setColor(Color.LIGHT_GRAY);
				
				gra.fill3DRect(tankX-15, tankY-15, 30, 30, false);//1:中
				gra.fill3DRect(tankX-15-5, tankY-15-20, 5, 20, false);//2
				gra.fill3DRect(tankX+15, tankY-15-20, 5, 20, false);//3
				gra.fill3DRect(tankX-15-5, tankY+15, 5, 20, false);//4
				gra.fill3DRect(tankX+15, tankY+15, 5, 20, false);//5
				gra.fill3DRect(tankX-15-5, tankY-15, 5, 30, false);//6
				gra.fill3DRect(tankX+15, tankY-15, 5, 30, false);//7
				
				gra.fill3DRect(tankX-15, tankY+24, 30, 8, false);//8
				gra.fill3DRect(tankX-15, tankY+15, 30, 10, false);//9
				gra.setColor(Color.DARK_GRAY);
				gra.fill3DRect(tankX+11, tankY + 15, 2, 8, false);//10 in 9
				gra.setColor(Color.LIGHT_GRAY);
				gra.fill3DRect(tankX-15, tankY-35, 30, 20, false);//11 trail
				/*装饰品*/
				gra.fill3DRect(tankX-15, tankY-33, 10, 6, true);//12
				gra.fill3DRect(tankX+5, tankY-33, 10, 6, true);//13
				gra.setColor(Color.DARK_GRAY);
				gra.drawLine(tankX-14, tankY-33, tankX-14, tankY-33+6);
				gra.drawLine(tankX-12, tankY-33, tankX-12, tankY-33+6);
				gra.drawLine(tankX-10, tankY-33, tankX-10, tankY-33+6);
				gra.drawLine(tankX-8,  tankY-33, tankX-8, tankY-33+6);
				
				gra.drawLine(tankX+6, tankY-33, tankX+6, tankY-33+6);
				gra.drawLine(tankX+8, tankY-33, tankX+8, tankY-33+6);
				gra.drawLine(tankX+10, tankY-33, tankX+10, tankY-33+6);
				gra.drawLine(tankX+12,  tankY-33, tankX+12, tankY-33+6);
				
				gra.setColor(Color.DARK_GRAY);
				gra.fillRect(tankX-11, tankY-37, 5, 2);//14
				gra.fillRect(tankX+6, tankY-37, 5, 2);//15
				gra.fillOval(tankX + 3, tankY - 27, 10, 10);//16
				gra.fillOval(tankX - 2, tankY - 27, 4, 4);//17
				gra.setColor(Color.LIGHT_GRAY);
				gra.fillOval(tankX + 5, tankY - 24, 6, 6);//16

				//炮塔
				gra.setColor(Color.GRAY);
					
				gra.fill3DRect(tankX-15, tankY-17, 29, 33, true);//1:w外
				gra.fill3DRect(tankX-10, tankY-15, 19, 29, true);//2:内
				gra.setColor(Color.DARK_GRAY);
				gra.drawLine(tankX-15,  tankY-17, tankX-10, tankY-15);
				gra.drawLine(tankX+14,  tankY-17, tankX+9, tankY-15);
				gra.drawLine(tankX-14,  tankY+16, tankX-9, tankY+14);
				gra.drawLine(tankX+14,  tankY+16, tankX+9, tankY+14);
					
				tankY += 15;
					
				gra.setColor(Color.GRAY);
					
				gra.fill3DRect(tankX-3, tankY+12-6, 6, 5, true);//1:zhong
				gra.fill3DRect(tankX-2, tankY+37-26, 4, 25, true);//2:Gun
				xPoints[0] = tankX-3;
				yPoints[0] = tankY+6;
				xPoints[1] = tankX+3;
				yPoints[1] = tankY+6;
				xPoints[2] = tankX+6;
				yPoints[2] = tankY;
				xPoints[3] = tankX-6;
				yPoints[3] = tankY;
				gra.setColor(Color.DARK_GRAY);
				gra.fillPolygon(xPoints, yPoints, 4);
			}
		}
	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			hero.moveDown();
			System.out.println("s");
		}
		else if(e.getKeyCode() == KeyEvent.VK_D)
		{
			hero.moveRight();
			System.out.println("d");
		}
		else if(e.getKeyCode() == KeyEvent.VK_W)
		{
			hero.moveUp();
			System.out.println("w");
		}
		else if(e.getKeyCode() == KeyEvent.VK_A)
		{
			hero.moveLeft();
			System.out.println("a");
		}
		if(e.getKeyCode() == KeyEvent.VK_J)
		{
			if(hero.bullets.size() <= hero.bulletMax)
			{
				hero.shoot();
			}
			System.out.println("J");
		}
		this.repaint();
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			this.repaint();
		}
	}
	public void checkHit(Bullet b, Tank tank)
	{
		if(tank.tankName.equals("Jagdtiger"))
		{
			switch(tank.getDirection())
			{
			case 0:
			case 1:
				if((b.x>=tank.x-20)&&(b.x<=tank.x+20)&&(b.y<=tank.y+35)&&(b.y>=tank.y-35))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			case 2:
			case 3:
				if((b.x>=tank.x-35)&&(b.x<=tank.x+35)&&(b.y<=tank.y+20)&&(b.y>=tank.y-20))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			}
		}
		else if(tank.tankName.equals("IS3"))
		{
			switch(tank.getDirection())
			{
			case 0:
				if((b.x>=tank.x-20)&&(b.x<=tank.x+20)&&(b.y<=tank.y+44)&&(b.y>=tank.y-26))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			case 1:
				if((b.x>=tank.x-20)&&(b.x<=tank.x+20)&&(b.y<=tank.y+26)&&(b.y>=tank.y-44))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			case 2:
				if((b.x>=tank.x-26)&&(b.x<=tank.x+44)&&(b.y<=tank.y+20)&&(b.y>=tank.y-20))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			case 3:
				if((b.x>=tank.x-44)&&(b.x<=tank.x+26)&&(b.y<=tank.y+20)&&(b.y>=tank.y-20))
				{
					tank.isLive = false;
					b.isLive = false;
				}
				break;
			}
		
		}
	}
}
class EnemyTank extends Tank implements Runnable{

	//this.speed = 5;
	public EnemyTank(String name, int x, int y, int speed, int direction) {
		super(name, x, y, speed, direction);
		this.speed = 5;
		bulletMax = 4;
		// TODO Auto-generated constructor stub
	}

	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			int num = (int)(Math.random() * 100) / 4;
			switch(num)
			{
				case 0: 
					for (int i = 0; i < 10; i++) {
						moveUp();
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case 1: 
					for (int i = 0; i < 10; i++) {
						moveDown();
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case 2: 
					for (int i = 0; i < 10; i++) {
						moveLeft();
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case 3:
					for (int i = 0; i < 10; i++) {
						moveRight();
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
			}
			if(isLive == false) break;
			
		}//end of while
	}//run
	
}
class HeroTank extends Tank{

	public HeroTank(String name, int x, int y, int speed, int direction) {
		super(name, x, y, speed, direction);
		// TODO Auto-generated constructor stub
		bulletMax = 3;
	}
	
}

class Bullet implements Runnable{
	int x;
	int y;
	int speed;
	int direction;
	boolean isLive;
	public Bullet(int x, int y, int speed, int direction){
		isLive = true;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.direction = direction;
	}
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{	
			switch(direction)
			{
				case 0: y -= speed; break;
				case 1: y += speed; break;
				case 2: x -= speed; break;
				case 3: x += speed; break;
			}
			System.out.println("x:"+x+"y:"+y);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(x<0 || x>1000 || y<0 || y>600)
			{
				this.isLive = false;
				break;
			}
			if(this.isLive == false)
			 break;
		}
	}
}
class Tank{
	String tankName;
	int x, y;
	int speed, direction;
	int bulletMax;
	boolean isLive = true;
	//int maxBullet = 0;
	Vector<Bullet> bullets = null;
	Bullet bullet = null;

	public void shoot()
	{
		switch (direction) {
		case 0:
			bullet = new Bullet(x-2, y - 91, 25, direction);
			bullets.add(bullet);
			break;
		case 1:
			bullet = new Bullet(x-2, y + 55, 25, direction);
			bullets.add(bullet);
			break;
		case 2:
			bullet = new Bullet(x-76, y-23, 25, direction);
			bullets.add(bullet);
			break;
		case 3:
			bullet = new Bullet(x+76, y-23, 25, direction);
			bullets.add(bullet);
			break;

		default:
			break;
		}
		Thread thread = new Thread(bullet);
		thread.start();
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}


	public void moveUp()
	{
		y -= speed;
		direction = 0;
	}
	public void moveDown()
	{
		y += speed;
		direction = 1;
	}
	public void moveLeft()
	{
		x -= speed;
		direction = 2;
	}
	public void moveRight()
	{
		x += speed;
		direction = 3;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Tank(String tankName, int x, int y, int speed, int direction)
	{
		this.tankName = tankName;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.direction = direction;
		bullets = new Vector<Bullet>();

	}
	public String getTankName() {
		return tankName;
	}
	public void setTankName(String tankName) {
		this.tankName = tankName;
	}
}


