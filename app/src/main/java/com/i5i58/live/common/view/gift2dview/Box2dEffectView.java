package com.i5i58.live.common.view.gift2dview;

import android.content.Context;
import android.content.res.Configuration;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.i5i58.live.common.view.gift2dview.Beans.BallInfo;
import com.i5i58.live.common.view.gift2dview.Beans.Box2dConstant;
import com.i5i58.live.common.view.gift2dview.Tools.Transform;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by alex_xq on 15/6/19.
 */
public class Box2dEffectView implements ApplicationListener {

    private static final String TAG = "Box2dEffectView";
    private static final float PXTM = 30;
    private OrthographicCamera camera;
    private Box2DDebugRenderer m_debugRenderer;
    private World world;
    private Box2dSenserLogic m_box2dSenserLogic;
    private Context m_context;
    private List<Body> m_ballBodys = new ArrayList<>();
    private SpriteBatch m_spriteBatch;
    private boolean m_isDebugRenderer;
	private boolean m_candraw = true;
    Texture TESTTEXTURE;

    public Box2dEffectView(Context context) {
        m_context = context;
    }


	public void release(){m_box2dSenserLogic.release();}
    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        TESTTEXTURE = new Texture(Gdx.files.internal("mark/star.png"));

        float cameraWidth = w / PXTM;
        float cameraHeight = h / PXTM;
        camera = new OrthographicCamera(cameraWidth, cameraHeight);
        m_debugRenderer = new Box2DDebugRenderer();

        m_spriteBatch = new SpriteBatch();

        world = new World(new Vector2(0f, -40f), true);
        addground();
        addleftwall();
        addrighttwall();

        m_box2dSenserLogic = new Box2dSenserLogic(world, m_context);
    }

    @Override
    public void dispose() {

    }

	public void setCanDraw(boolean candraw) {
		m_candraw = candraw;

		if (!m_candraw) {
			_destoryAll();
		}
	}

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        synchronized (Box2dEffectView.class) {

	        if (!m_candraw) {
		        _destoryAll();
		        return;
	        }

            if (m_ballBodys.size() == 0)
                return;
            float deltatm = Gdx.app.getGraphics().getDeltaTime();
            world.step(deltatm, 6, 2);

//            Log.d(TAG, "deltatm:" + deltatm);
            _ballUpdatasLogic(deltatm);

            if (m_isDebugRenderer)
                m_debugRenderer.render(world, camera.combined);
        }
    }

    @Override
    public void resize(int width, int height) {
	    Configuration mConfiguration = m_context.getResources().getConfiguration(); //获取设置的配置信息
	    int ori = mConfiguration.orientation ; //获取屏幕方向

	    setIsPortrait(ori == Configuration.ORIENTATION_PORTRAIT);
    }


    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

	private void setIsPortrait(boolean isPortrait){
		if (m_box2dSenserLogic != null)
			m_box2dSenserLogic.setIsPortrait(isPortrait);
	}

    public void addball(boolean isleft) {

	    if (!m_candraw)
		    return;

        _totalLimitsLogic();

        synchronized (Box2dEffectView.class) {

            BodyDef BallBodydef = new BodyDef();
            BallBodydef.type = BodyDef.BodyType.DynamicBody;

            float thrownXRandom = (float) Math.random() * 26.0f + 4.0f;
            float thrownYRandom = -( (float) Math.random() * 15.0f + 3.0f );
            if (isleft) {
                BallBodydef.linearVelocity.set(thrownXRandom, thrownYRandom);
                BallBodydef.position.set(new Vector2(-camera.viewportWidth/2 + 2f, camera.viewportHeight/2 - 2f) );

            } else {
                BallBodydef.linearVelocity.set(-thrownXRandom, thrownYRandom);
                BallBodydef.position.set(new Vector2(camera.viewportWidth/2 - 2f, camera.viewportHeight/2 - 2f) );
            }

            Body BallBody = world.createBody(BallBodydef);
            BallBody.setUserData(new BallInfo());
            BallBody.setFixedRotation(false);
            CircleShape shape = new CircleShape();
            shape.setRadius(1f);
            FixtureDef BallFixtureDef = new FixtureDef();
            BallFixtureDef.shape = shape;
            BallFixtureDef.density = 1.5f;
            BallFixtureDef.friction = 0.3f;
            BallFixtureDef.restitution = 0.5f; // Make it bounce a little bit
            BallBody.createFixture(BallFixtureDef);
            shape.dispose();

            m_ballBodys.add(BallBody);

            if (m_ballBodys.size() == 1)
                m_box2dSenserLogic.startListener();
        }
    }

    private void addground(){
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(0, -camera.viewportHeight/2));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth, 1.0f / PXTM);
        groundBody.createFixture(groundBox, 0.5f);
        groundBox.dispose();
    }

    private void addleftwall(){
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(-camera.viewportWidth/2, 0f));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(1.0f / PXTM, camera.viewportHeight);
        groundBody.createFixture(groundBox, 0.5f);
        groundBox.dispose();
    }

    private void addrighttwall(){
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(camera.viewportWidth/2, 0f));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(1.0f / PXTM, camera.viewportHeight);
        groundBody.createFixture(groundBox, 0.5f);
        groundBox.dispose();
    }

    private void _totalLimitsLogic(){

        synchronized (Box2dEffectView.class){
            if (m_ballBodys.size() > Box2dConstant.MaxBallCounter){
                destoryBody(0);
            }
        }

    }

	private void _destoryAll(){

		synchronized (Box2dEffectView.class){
			for (int i=0; i<m_ballBodys.size();i++){
				destoryBody(i);
				i--;
			}
		}

	}

    private void _ballUpdatasLogic(float deltatm){

        for (int i=0; i<m_ballBodys.size(); i++){

            Body bd = m_ballBodys.get(i);
            BallInfo ballInfo = (BallInfo)bd.getUserData();
            float alphascale = 1f;
            float curruntm = ballInfo.getRuntimes();

            if (curruntm >= Box2dConstant.MaxBallLiferSP){
                alphascale = hidesmallBody(i);
            }
            //判断是否已经到了生命尽头
            if (curruntm >= Box2dConstant.MaxBallLifer){
                destoryBody(i);
                i--;
                continue;
            }

            //更新时间
            ballInfo.setRuntimes(curruntm + deltatm);

            Vector2 transformVect = Transform.mtp(bd.getPosition().x + 1f, bd.getPosition().y + 1f, new Vector2(2f, 2f), PXTM);
            //绘制
            m_spriteBatch.begin();
            m_spriteBatch.setColor(new Color(1,1,1,alphascale));
            m_spriteBatch.draw(TESTTEXTURE, transformVect.x+(1f-alphascale)*30, transformVect.y+(1f-alphascale)*30, alphascale*60f, alphascale*60f);
            m_spriteBatch.end();

        }
    }

    private float hidesmallBody( int indexofbodys){
        BallInfo ballInfo = (BallInfo)m_ballBodys.get(indexofbodys).getUserData();
        float als = ballInfo.getAplhascale();
        als = als - 0.02f;
        if (als <= 0)
            return 0;
        ballInfo.setAplhascale(als);
        return  als;
    }

    private void destoryBody(int indexofbodys){
        Body body = m_ballBodys.remove(indexofbodys);
        world.destroyBody(body);

        if (m_ballBodys.size() == 0){
            m_box2dSenserLogic.stopListener();
        }
    }

    public void openDebugRenderer(boolean debugRenderer) {
        m_isDebugRenderer = debugRenderer;
    }
}