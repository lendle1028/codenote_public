-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2022-03-15 09:50:27
-- 伺服器版本： 10.4.22-MariaDB
-- PHP 版本： 8.1.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫: `codenotedata`
--

-- --------------------------------------------------------

--
-- 資料表結構 `note_info_bean`
--

CREATE TABLE `note_info_bean` (
  `noteid` varchar(255) NOT NULL,
  `note_content` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 資料表結構 `project_bean`
--

CREATE TABLE `project_bean` (
  `projectid` varchar(255) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `prjectname` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 資料表結構 `project_bean_codeinfoes`
--

CREATE TABLE `project_bean_codeinfoes` (
  `project_bean_projectid` varchar(255) NOT NULL,
  `codeinfoes_codeid` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 資料表結構 `sample_code_bean`
--

CREATE TABLE `sample_code_bean` (
  `dtype` varchar(31) NOT NULL,
  `codeid` varchar(255) NOT NULL,
  `code_content` longtext DEFAULT NULL,
  `code_nmae` varchar(255) DEFAULT NULL,
  `note_info_bean_noteid` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `sample_code_bean`
--

INSERT INTO `sample_code_bean` (`dtype`, `codeid`, `code_content`, `code_nmae`, `note_info_bean_noteid`) VALUES
('SampleCodeBean', '01783354-0230-465e-8535-8ea86bcae88c', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene_answer.js\"></script>\r\n    <!-- <script src=\"./Scene.js\"></script> -->\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 600,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>', 'index.html', NULL),
('SampleCodeBean', '058cb757-1fa7-431e-9329-aff3232b195d', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.player = null;\r\n    this.cursors = null;\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n    this.tileSprite = null;\r\n    this.score = 0;\r\n  }\r\n  preload() {\r\n    this.load.image(\"bg\", \"https://cdn.glitch.me/66abd8d6-7eba-4e23-86a5-0b1775f56a7d/download%20(2).png?v=1640745974529\");\r\n    this.load.image(\"obstacle\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    this.tileSprite = this.add.tileSprite(0, 0, 1600, 800, \"bg\");\r\n    //hint: 利用 repeat 和 setXY 建立三組不同間距的障礙物（obstacle）\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n\r\n    this.player = new Player(this, 60, 350);\r\n\r\n    //主角、怪物等等通常會抽出去\r\n    \r\n    this.cursors = this.input.keyboard.createCursorKeys();\r\n    //hint: 在三羣障礙物和 player 之間建立 collider，並導向到 this.collision callback\r\n    \r\n    /*********************************************************************************************************************** */\r\n    //https://rexrainbow.github.io/phaser3-rex-notes/docs/site/text/\r\n    this.scoreText = this.add.text(10, 10, \"score: 0\", {\r\n      fontSize: \"32px\",\r\n      color: \"#ffffff\"\r\n    });\r\n\r\n    this.time.addEvent({\r\n      delay: 1000,\r\n      callback: this.updateCounter,\r\n      callbackScope: this,\r\n      loop: true\r\n    });\r\n  }\r\n\r\n  collision(player, obstacle) {\r\n    obstacle.disableBody(true, true);\r\n  }\r\n\r\n  /*********************************************************************************************************************** */\r\n  updateCounter() {\r\n    if (!this.finish) {\r\n      this.score = this.score + 1;\r\n    }\r\n  }\r\n\r\n  update() {\r\n    /*********************************************************************************************************************** */\r\n    this.scoreText.setText(\"Score: \" + this.score);\r\n    //hint：讓背景橫向捲動\r\n   \r\n    this.obstacle1s.setVelocityX(-100);\r\n    this.obstacle2s.setVelocityX(-200);\r\n    this.obstacle3s.setVelocityX(-200);\r\n\r\n    if (this.cursors.up.isDown) {\r\n      this.player.setVelocityY(-100);\r\n    } else if (this.cursors.down.isDown) {\r\n      this.player.setVelocityY(100);\r\n    }\r\n  }\r\n}\r\n', 'Scene.js', NULL),
('SampleCodeBean', '29a5bcc9-d05f-4b65-b5a7-5c2244f5ca88', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.player = null;\r\n    this.cursors = null;\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n    this.tileSprite = null;\r\n    this.score = 0;\r\n  }\r\n  preload() {\r\n    this.load.image(\"bg\", \"https://cdn.glitch.me/66abd8d6-7eba-4e23-86a5-0b1775f56a7d/download%20(2).png?v=1640745974529\");\r\n    this.load.image(\"obstacle\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    this.tileSprite = this.add.tileSprite(0, 0, 1600, 800, \"bg\");\r\n    //hint: 利用 repeat 和 setXY 建立三組不同間距的障礙物（obstacle）\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n\r\n    this.player = new Player(this, 60, 350);\r\n\r\n    //主角、怪物等等通常會抽出去\r\n    \r\n    this.cursors = this.input.keyboard.createCursorKeys();\r\n    //hint: 在三羣障礙物和 player 之間建立 collider，並導向到 this.collision callback\r\n    \r\n    /*********************************************************************************************************************** */\r\n    //https://rexrainbow.github.io/phaser3-rex-notes/docs/site/text/\r\n    this.scoreText = this.add.text(10, 10, \"score: 0\", {\r\n      fontSize: \"32px\",\r\n      color: \"#ffffff\"\r\n    });\r\n\r\n    this.time.addEvent({\r\n      delay: 1000,\r\n      callback: this.updateCounter,\r\n      callbackScope: this,\r\n      loop: true\r\n    });\r\n  }\r\n\r\n  collision(player, obstacle) {\r\n    obstacle.disableBody(true, true);\r\n  }\r\n\r\n  /*********************************************************************************************************************** */\r\n  updateCounter() {\r\n    if (!this.finish) {\r\n      this.score = this.score + 1;\r\n    }\r\n  }\r\n\r\n  update() {\r\n    /*********************************************************************************************************************** */\r\n    this.scoreText.setText(\"Score: \" + this.score);\r\n    //hint：讓背景橫向捲動\r\n   \r\n    this.obstacle1s.setVelocityX(-100);\r\n    this.obstacle2s.setVelocityX(-200);\r\n    this.obstacle3s.setVelocityX(-200);\r\n\r\n    if (this.cursors.up.isDown) {\r\n      this.player.setVelocityY(-100);\r\n    } else if (this.cursors.down.isDown) {\r\n      this.player.setVelocityY(100);\r\n    }\r\n  }\r\n}\r\n', 'Scene.js', NULL),
('SampleCodeBean', '2c0919ec-bd11-4284-9eac-1a5fc2d2173b', 'class Player extends Phaser.Physics.Arcade.Sprite {\r\n  constructor(scene, x, y) {\r\n    super(scene, x, y, \"mario\");\r\n    this.scene = scene;\r\n    scene.physics.world.enable(this);\r\n    scene.add.existing(this);\r\n    this.setBounce(1.0);\r\n    this.setCollideWorldBounds(true);\r\n    this.setGravityY(200);\r\n  }\r\n}\r\n', 'Player.js', NULL),
('SampleCodeBean', '2c391b43-db34-465e-a394-061e13f4381d', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene.js\"></script>\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 600,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>', 'index.html', NULL),
('SampleCodeBean', '2d7bf9c4-4a76-45be-92c4-a54b9d224b51', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene.js\"></script>\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 400,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL),
('SampleCodeBean', '38745b50-a32b-4edb-9da5-9935985f4ad4', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene_answer.js\"></script>\r\n    <!-- <script src=\"./Scene.js\"></script> -->\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 600,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>', 'index.html', NULL),
('SampleCodeBean', '4ad1d5d2-6be1-4fce-b097-51b990277589', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.planet = null;\r\n  }\r\n  preload() {\r\n    this.load.image(\"star\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    \r\n    this.planet = this.physics.add.staticGroup();\r\n    for(let i=0; i<20; i++){\r\n      //hint: 隨機生出很多的 star，隨機放置他們的位置\r\n    }\r\n    \r\n    //hint: 把 mario 放在畫面上的某個地方\r\n    this.player = null;\r\n    this.player.setVelocity(50, 50);\r\n    //hint: 在 mario 和 star 之間建立 collider\r\n    \r\n    \r\n  }\r\n\r\n  update() {\r\n    \r\n  }\r\n}\r\n', 'Scene.js', NULL),
('SampleCodeBean', '576dd1a5-c7ac-4b46-95f9-998b6fcdb755', '<!DOCTYPE html>\r\n<!--\r\n目標：讓 mario 可以左右移動，並在接觸到 star 的時候，讓 star 消失，\r\n且分數 +1\r\n-->\r\n<html lang=\"en\">\r\n  <head>\r\n    <title>Hello!</title>\r\n    <meta charset=\"utf-8\" />\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\r\n  </head>\r\n  <body>\r\n    <span id=\"score\"></span><br/>\r\n    <img style=\"display: none\" id=\"mario\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/pngaaa.com-1198218.png?v=1640742982064\"/>\r\n    <img style=\"display: none\" id=\"star\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\"/>\r\n    <canvas id=\"c\" width=\"500\" height=\"500\"></canvas>\r\n    <script>\r\n      let score=0;\r\n      let canvas = document.getElementById(\"c\");\r\n      let mario=document.getElementById(\"mario\");\r\n      let star=document.getElementById(\"star\");\r\n      let ctx = canvas.getContext(\"2d\");\r\n\r\n      let components = [];\r\n      let round = 0;\r\n      function loop() {\r\n        document.getElementById(\"score\").innerHTML=\"\"+score;\r\n        ctx.save();\r\n        ctx.clearRect(0, 0, canvas.width, canvas.height);\r\n        \r\n        for (let component of components) {\r\n          if (component.getType() == \"Obstacle\") {\r\n            if (round++ % 3 == 0 && Math.random() < 0.4) {\r\n              component.y = component.y + 5;\r\n              if (Math.random() < 0.2) {\r\n                component.x = component.x + 1;\r\n              } else if (Math.random() > 0.8) {\r\n                component.x = component.x - 1;\r\n              }\r\n            }\r\n          }\r\n          component.update();\r\n        }\r\n        //now, we check overlap between all pairs of components\r\n        outer: for(let i=0; i<components.length; i++){\r\n          for(let j=i+1; j<components.length; j++){\r\n            if(!components[i].visible || !components[j].visible){\r\n              //then skip\r\n              continue;\r\n            }\r\n            if(isOverlap(components[i], components[j])){\r\n              //hint: 檢查碰撞的雙方，進行條件及分數的判定\r\n            }\r\n          }\r\n        }\r\n        let oldComponents=components;\r\n        components=[];\r\n        //check game state & collect new sets of components to save memory\r\n        for(let component of oldComponents){\r\n          if(!component.visible){\r\n            continue;\r\n          }\r\n          \r\n          if(component.y>=0 && component.y<=canvas.height){\r\n            components.push(component);\r\n          }\r\n        }\r\n        \r\n        ctx.restore();\r\n        if (true) {\r\n          setTimeout(function() {\r\n            window.requestAnimationFrame(loop);\r\n          }, 1000 / 25);\r\n        }\r\n      }\r\n      window.requestAnimationFrame(loop);\r\n\r\n      class Component {\r\n        constructor(x, y, width, height) {\r\n          this.x = x;\r\n          this.y = y;\r\n          this.width = width;\r\n          this.height = height;\r\n          this.visible = true;\r\n        }\r\n        getType() {\r\n          return \"unknown\";\r\n        }\r\n        update() {}\r\n      }\r\n\r\n      class MainComponent extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 100, 150);\r\n        }\r\n        getType() {\r\n          return \"MainComponent\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(mario, 0, 0, 200, 307, this.x, this.y, 50, 80);\r\n          }\r\n        }\r\n      }\r\n\r\n      class Obstacle extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 30, 30);\r\n        }\r\n        getType() {\r\n          return \"Obstacle\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(star, 0, 0, 30, 30, this.x, this.y, 30, 30);\r\n          }\r\n        }\r\n      }\r\n\r\n      function isOverlap(obj1, obj2) {\r\n        let overlap = !(\r\n          obj1.x + obj1.width < obj2.x ||\r\n          obj1.x > obj2.x + obj2.width ||\r\n          obj1.y + obj1.height < obj2.y ||\r\n          obj1.y > obj2.y + obj2.height\r\n        );\r\n        return overlap;\r\n      }\r\n\r\n      let mainComponent = new MainComponent(\r\n        canvas.width / 2,\r\n        canvas.height/2\r\n      );\r\n      components.push(mainComponent);\r\n      for (let y = 0; y < 10; y++) {\r\n        for (let x = 0; x < 5; x++) {\r\n          let ob1 = new Obstacle(\r\n            Math.floor(Math.random() * canvas.width),\r\n            (y + 1) * 5\r\n          );\r\n          components.push(ob1);\r\n        }\r\n      }\r\n\r\n      function onKeyDown(e) {\r\n        //hint: 讓 mario 可以左右移動\r\n      }\r\n      window.addEventListener(\"keydown\", onKeyDown);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL),
('SampleCodeBean', '64f09b71-6a2f-4afe-9df9-97cd0e5ffc06', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.planet = null;\r\n  }\r\n  preload() {\r\n    this.load.image(\"star\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    \r\n    this.planet = this.physics.add.staticGroup();\r\n    for(let i=0; i<20; i++){\r\n      //hint: 隨機生出很多的 star，隨機放置他們的位置\r\n    }\r\n    \r\n    //hint: 把 mario 放在畫面上的某個地方\r\n    this.player = null;\r\n    this.player.setVelocity(50, 50);\r\n    //hint: 在 mario 和 star 之間建立 collider\r\n    \r\n    \r\n  }\r\n\r\n  update() {\r\n    \r\n  }\r\n}\r\n', 'Scene.js', NULL),
('SampleCodeBean', '6f951eca-1dbb-4e0e-93a5-8584f827aa1f', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene.js\"></script>\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 400,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL),
('SampleCodeBean', '8c836d0b-ea65-4536-bd6e-79b9341c174c', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.player = null;\r\n    this.cursors = null;\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n    this.tileSprite = null;\r\n    this.score = 0;\r\n  }\r\n  preload() {\r\n    this.load.image(\"bg\", \"https://cdn.glitch.me/66abd8d6-7eba-4e23-86a5-0b1775f56a7d/download%20(2).png?v=1640745974529\");\r\n    this.load.image(\"obstacle\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    this.tileSprite = this.add.tileSprite(0, 0, 1600, 800, \"bg\");\r\n    //hint: 利用 repeat 和 setXY 建立三組不同間距的障礙物（obstacle）\r\n    this.obstacle1s = null;\r\n    this.obstacle2s = null;\r\n    this.obstacle3s = null;\r\n\r\n    this.player = new Player(this, 60, 350);\r\n\r\n    //主角、怪物等等通常會抽出去\r\n    \r\n    this.cursors = this.input.keyboard.createCursorKeys();\r\n    //hint: 在三羣障礙物和 player 之間建立 collider，並導向到 this.collision callback\r\n    \r\n    /*********************************************************************************************************************** */\r\n    //https://rexrainbow.github.io/phaser3-rex-notes/docs/site/text/\r\n    this.scoreText = this.add.text(10, 10, \"score: 0\", {\r\n      fontSize: \"32px\",\r\n      color: \"#ffffff\"\r\n    });\r\n\r\n    this.time.addEvent({\r\n      delay: 1000,\r\n      callback: this.updateCounter,\r\n      callbackScope: this,\r\n      loop: true\r\n    });\r\n  }\r\n\r\n  collision(player, obstacle) {\r\n    obstacle.disableBody(true, true);\r\n  }\r\n\r\n  /*********************************************************************************************************************** */\r\n  updateCounter() {\r\n    if (!this.finish) {\r\n      this.score = this.score + 1;\r\n    }\r\n  }\r\n\r\n  update() {\r\n    /*********************************************************************************************************************** */\r\n    this.scoreText.setText(\"Score: \" + this.score);\r\n    //hint：讓背景橫向捲動\r\n   \r\n    this.obstacle1s.setVelocityX(-100);\r\n    this.obstacle2s.setVelocityX(-200);\r\n    this.obstacle3s.setVelocityX(-200);\r\n\r\n    if (this.cursors.up.isDown) {\r\n      this.player.setVelocityY(-100);\r\n    } else if (this.cursors.down.isDown) {\r\n      this.player.setVelocityY(100);\r\n    }\r\n  }\r\n}\r\n', 'Scene.js', NULL),
('SampleCodeBean', 'b0183b0d-4b77-4817-a42c-d29db5079758', '/**\r\n * 定義一個 scene，用成員變數儲存 scene 上面的物件\r\n * override preload, create, update 函式\r\n */\r\nclass Scene extends Phaser.Scene {\r\n  /*********************************************************************************************************************** */\r\n  constructor() {\r\n    super();\r\n    this.planet = null;\r\n  }\r\n  preload() {\r\n    this.load.image(\"star\", \"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\");\r\n    this.load.image(\"mario\", \"https://cdn.glitch.me/22012775-abc2-4200-b16c-1e640ccc259f/mario.png?v=1640745025914\");\r\n  }\r\n  create() {\r\n    \r\n    this.planet = this.physics.add.staticGroup();\r\n    for(let i=0; i<20; i++){\r\n      //hint: 隨機生出很多的 star，隨機放置他們的位置\r\n    }\r\n    \r\n    //hint: 把 mario 放在畫面上的某個地方\r\n    this.player = null;\r\n    this.player.setVelocity(50, 50);\r\n    //hint: 在 mario 和 star 之間建立 collider\r\n    \r\n    \r\n  }\r\n\r\n  update() {\r\n    \r\n  }\r\n}', 'Scene.js', NULL),
('SampleCodeBean', 'bf21e8c3-302d-40eb-86ae-59e502054455', '<!DOCTYPE html>\r\n<!--\r\n目標：讓 mario 可以左右移動，並在接觸到 star 的時候，讓 star 消失，\r\n且分數 +1\r\n-->\r\n<html lang=\"en\">\r\n  <head>\r\n    <title>Hello!</title>\r\n    <meta charset=\"utf-8\" />\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\r\n  </head>\r\n  <body>\r\n    <span id=\"score\"></span><br/>\r\n    <img style=\"display: none\" id=\"mario\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/pngaaa.com-1198218.png?v=1640742982064\"/>\r\n    <img style=\"display: none\" id=\"star\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\"/>\r\n    <canvas id=\"c\" width=\"500\" height=\"500\"></canvas>\r\n    <script>\r\n      let score=0;\r\n      let canvas = document.getElementById(\"c\");\r\n      let mario=document.getElementById(\"mario\");\r\n      let star=document.getElementById(\"star\");\r\n      let ctx = canvas.getContext(\"2d\");\r\n\r\n      let components = [];\r\n      let round = 0;\r\n      function loop() {\r\n        document.getElementById(\"score\").innerHTML=\"\"+score;\r\n        ctx.save();\r\n        ctx.clearRect(0, 0, canvas.width, canvas.height);\r\n        \r\n        for (let component of components) {\r\n          if (component.getType() == \"Obstacle\") {\r\n            if (round++ % 3 == 0 && Math.random() < 0.4) {\r\n              component.y = component.y + 5;\r\n              if (Math.random() < 0.2) {\r\n                component.x = component.x + 1;\r\n              } else if (Math.random() > 0.8) {\r\n                component.x = component.x - 1;\r\n              }\r\n            }\r\n          }\r\n          component.update();\r\n        }\r\n        //now, we check overlap between all pairs of components\r\n        outer: for(let i=0; i<components.length; i++){\r\n          for(let j=i+1; j<components.length; j++){\r\n            if(!components[i].visible || !components[j].visible){\r\n              //then skip\r\n              continue;\r\n            }\r\n            if(isOverlap(components[i], components[j])){\r\n              //hint: 檢查碰撞的雙方，進行條件及分數的判定\r\n            }\r\n          }\r\n        }\r\n        let oldComponents=components;\r\n        components=[];\r\n        //check game state & collect new sets of components to save memory\r\n        for(let component of oldComponents){\r\n          if(!component.visible){\r\n            continue;\r\n          }\r\n          \r\n          if(component.y>=0 && component.y<=canvas.height){\r\n            components.push(component);\r\n          }\r\n        }\r\n        \r\n        ctx.restore();\r\n        if (true) {\r\n          setTimeout(function() {\r\n            window.requestAnimationFrame(loop);\r\n          }, 1000 / 25);\r\n        }\r\n      }\r\n      window.requestAnimationFrame(loop);\r\n\r\n      class Component {\r\n        constructor(x, y, width, height) {\r\n          this.x = x;\r\n          this.y = y;\r\n          this.width = width;\r\n          this.height = height;\r\n          this.visible = true;\r\n        }\r\n        getType() {\r\n          return \"unknown\";\r\n        }\r\n        update() {}\r\n      }\r\n\r\n      class MainComponent extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 100, 150);\r\n        }\r\n        getType() {\r\n          return \"MainComponent\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(mario, 0, 0, 200, 307, this.x, this.y, 50, 80);\r\n          }\r\n        }\r\n      }\r\n\r\n      class Obstacle extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 30, 30);\r\n        }\r\n        getType() {\r\n          return \"Obstacle\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(star, 0, 0, 30, 30, this.x, this.y, 30, 30);\r\n          }\r\n        }\r\n      }\r\n\r\n      function isOverlap(obj1, obj2) {\r\n        let overlap = !(\r\n          obj1.x + obj1.width < obj2.x ||\r\n          obj1.x > obj2.x + obj2.width ||\r\n          obj1.y + obj1.height < obj2.y ||\r\n          obj1.y > obj2.y + obj2.height\r\n        );\r\n        return overlap;\r\n      }\r\n\r\n      let mainComponent = new MainComponent(\r\n        canvas.width / 2,\r\n        canvas.height/2\r\n      );\r\n      components.push(mainComponent);\r\n      for (let y = 0; y < 10; y++) {\r\n        for (let x = 0; x < 5; x++) {\r\n          let ob1 = new Obstacle(\r\n            Math.floor(Math.random() * canvas.width),\r\n            (y + 1) * 5\r\n          );\r\n          components.push(ob1);\r\n        }\r\n      }\r\n\r\n      function onKeyDown(e) {\r\n        //hint: 讓 mario 可以左右移動\r\n      }\r\n      window.addEventListener(\"keydown\", onKeyDown);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL),
('SampleCodeBean', 'e2d0aa3b-42db-43d0-94b6-33558992f60b', 'class Player extends Phaser.Physics.Arcade.Sprite {\r\n  constructor(scene, x, y) {\r\n    super(scene, x, y, \"mario\");\r\n    this.scene = scene;\r\n    scene.physics.world.enable(this);\r\n    scene.add.existing(this);\r\n    this.setBounce(1.0);\r\n    this.setCollideWorldBounds(true);\r\n    this.setGravityY(200);\r\n  }\r\n}\r\n', 'Player.js', NULL),
('SampleCodeBean', 'ef84f163-9fc4-48d6-b375-f1b86250259a', '<!DOCTYPE html>\r\n<!--\r\n目標：讓 mario 可以左右移動，並在接觸到 star 的時候，讓 star 消失，\r\n且分數 +1\r\n-->\r\n<html lang=\"en\">\r\n  <head>\r\n    <title>Hello!</title>\r\n    <meta charset=\"utf-8\" />\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\r\n  </head>\r\n  <body>\r\n    <span id=\"score\"></span><br/>\r\n    <img style=\"display: none\" id=\"mario\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/pngaaa.com-1198218.png?v=1640742982064\"/>\r\n    <img style=\"display: none\" id=\"star\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\"/>\r\n    <canvas id=\"c\" width=\"500\" height=\"500\"></canvas>\r\n    <script>\r\n      let score=0;\r\n      let canvas = document.getElementById(\"c\");\r\n      let mario=document.getElementById(\"mario\");\r\n      let star=document.getElementById(\"star\");\r\n      let ctx = canvas.getContext(\"2d\");\r\n\r\n      let components = [];\r\n      let round = 0;\r\n      function loop() {\r\n        document.getElementById(\"score\").innerHTML=\"\"+score;\r\n        ctx.save();\r\n        ctx.clearRect(0, 0, canvas.width, canvas.height);\r\n        \r\n        for (let component of components) {\r\n          if (component.getType() == \"Obstacle\") {\r\n            if (round++ % 3 == 0 && Math.random() < 0.4) {\r\n              component.y = component.y + 5;\r\n              if (Math.random() < 0.2) {\r\n                component.x = component.x + 1;\r\n              } else if (Math.random() > 0.8) {\r\n                component.x = component.x - 1;\r\n              }\r\n            }\r\n          }\r\n          component.update();\r\n        }\r\n        //now, we check overlap between all pairs of components\r\n        outer: for(let i=0; i<components.length; i++){\r\n          for(let j=i+1; j<components.length; j++){\r\n            if(!components[i].visible || !components[j].visible){\r\n              //then skip\r\n              continue;\r\n            }\r\n            if(isOverlap(components[i], components[j])){\r\n              //hint: 檢查碰撞的雙方，進行條件及分數的判定\r\n            }\r\n          }\r\n        }\r\n        let oldComponents=components;\r\n        components=[];\r\n        //check game state & collect new sets of components to save memory\r\n        for(let component of oldComponents){\r\n          if(!component.visible){\r\n            continue;\r\n          }\r\n          \r\n          if(component.y>=0 && component.y<=canvas.height){\r\n            components.push(component);\r\n          }\r\n        }\r\n        \r\n        ctx.restore();\r\n        if (true) {\r\n          setTimeout(function() {\r\n            window.requestAnimationFrame(loop);\r\n          }, 1000 / 25);\r\n        }\r\n      }\r\n      window.requestAnimationFrame(loop);\r\n\r\n      class Component {\r\n        constructor(x, y, width, height) {\r\n          this.x = x;\r\n          this.y = y;\r\n          this.width = width;\r\n          this.height = height;\r\n          this.visible = true;\r\n        }\r\n        getType() {\r\n          return \"unknown\";\r\n        }\r\n        update() {}\r\n      }\r\n\r\n      class MainComponent extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 100, 150);\r\n        }\r\n        getType() {\r\n          return \"MainComponent\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(mario, 0, 0, 200, 307, this.x, this.y, 50, 80);\r\n          }\r\n        }\r\n      }\r\n\r\n      class Obstacle extends Component {\r\n        constructor(x, y) {\r\n          super(x, y, 30, 30);\r\n        }\r\n        getType() {\r\n          return \"Obstacle\";\r\n        }\r\n        update() {\r\n          if (this.visible) {\r\n            ctx.drawImage(star, 0, 0, 30, 30, this.x, this.y, 30, 30);\r\n          }\r\n        }\r\n      }\r\n\r\n      function isOverlap(obj1, obj2) {\r\n        let overlap = !(\r\n          obj1.x + obj1.width < obj2.x ||\r\n          obj1.x > obj2.x + obj2.width ||\r\n          obj1.y + obj1.height < obj2.y ||\r\n          obj1.y > obj2.y + obj2.height\r\n        );\r\n        return overlap;\r\n      }\r\n\r\n      let mainComponent = new MainComponent(\r\n        canvas.width / 2,\r\n        canvas.height/2\r\n      );\r\n      components.push(mainComponent);\r\n      for (let y = 0; y < 10; y++) {\r\n        for (let x = 0; x < 5; x++) {\r\n          let ob1 = new Obstacle(\r\n            Math.floor(Math.random() * canvas.width),\r\n            (y + 1) * 5\r\n          );\r\n          components.push(ob1);\r\n        }\r\n      }\r\n\r\n      function onKeyDown(e) {\r\n        //hint: 讓 mario 可以左右移動\r\n      }\r\n      window.addEventListener(\"keydown\", onKeyDown);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL),
('SampleCodeBean', 'f421a1ea-6627-431f-ba11-c45e05664812', 'class Player extends Phaser.Physics.Arcade.Sprite {\r\n  constructor(scene, x, y) {\r\n    super(scene, x, y, \"mario\");\r\n    this.scene = scene;\r\n    scene.physics.world.enable(this);\r\n    scene.add.existing(this);\r\n    this.setBounce(1.0);\r\n    this.setCollideWorldBounds(true);\r\n    this.setGravityY(200);\r\n  }\r\n}\r\n', 'Player.js', NULL),
('SampleCodeBean', 'fc1469f7-f716-43e4-9057-304fe5401011', '<html>\r\n  <head>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/phaser@3.15.1/dist/phaser-arcade-physics.min.js\"></script>\r\n    <script src=\"./Scene.js\"></script>\r\n    <script src=\"./Player.js\"></script>\r\n  </head>\r\n\r\n  <body>\r\n    <script>\r\n      var config = {\r\n        type: Phaser.AUTO,\r\n        width: 800,\r\n        height: 400,\r\n        physics: {\r\n          default: \"arcade\",\r\n          arcade: {\r\n            debug: false\r\n          }\r\n        },\r\n        scene: new Scene()\r\n      };\r\n      var game = new Phaser.Game(config);\r\n    </script>\r\n  </body>\r\n</html>\r\n', 'index.html', NULL);

-- --------------------------------------------------------

--
-- 資料表結構 `sample_project_bean`
--

CREATE TABLE `sample_project_bean` (
  `projectid_ex` varchar(255) NOT NULL,
  `projectname_ex` varchar(255) DEFAULT NULL,
  `sampleprojectnote_path` varchar(255) DEFAULT NULL,
  `sampleproject_learninglevel` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `sample_project_bean`
--

INSERT INTO `sample_project_bean` (`projectid_ex`, `projectname_ex`, `sampleprojectnote_path`, `sampleproject_learninglevel`) VALUES
('0ab02719-8721-416e-ba2a-027713634404', '2.Introduction to Phaser', 'https://docs.google.com/document/d/1avu_7lCIgq0I02yLkFY8UozK1dXIESQnzCe9ZUgwTKk/edit?usp=sharing', 'Surfacelearning'),
('38889cb6-9e26-47c6-8434-aa8fd442db19', '3. Phaser Game_看!有飛碟', 'https://docs.google.com/document/d/1SoBGtJprTvFLdMmkn4uPjGhPdn5_w3vI_GZuYLjHPHg/edit?usp=sharing', 'Deeplearning'),
('79903bef-6ec6-4a0a-a426-3a8bf742912d', '2.Introduction to Phaser', 'https://docs.google.com/document/d/15ooIwP0kK43dxQLHLZ32yCv91VBkMHPka0d9HCfRsxs/edit?usp=sharing', 'Strategiclearning'),
('7a138710-a593-4b7c-b17e-6da5a1bce954', '1. Game Elements with Canvas_deeplearning', 'https://docs.google.com/document/d/1Y4IavbZhE7Znn8oiUx3Q3EzI0nSt1jDaFCFDmOsd_R4/edit?usp=sharing', 'Strategiclearning'),
('b200b707-6828-4da9-891d-0128c3fb499d', '1. Game Elements with Canvas_deeplearning', 'https://docs.google.com/document/d/1ekdrjfPiClWR1VI0fFwV3MLS1V9EbrpJ2VUUesDo0Ag/edit?usp=sharing', 'Surfacelearning'),
('b5b6adb9-3d4b-40ee-8e34-a59d651a455f', '3. Phaser Game_看!有飛碟', 'https://docs.google.com/document/d/18RdhxFMknDqXacX9Of5Yt9y5cyyI0jCTEXwCfHDEVqI/edit?usp=sharing', 'Strategiclearning'),
('b929da83-c3fc-451a-99da-999a66debb60', '2.Introduction to Phaser', 'https://docs.google.com/document/d/1_Lz36zJDH7FVIBGfPU8nMTm_dEAXH2IHQ5A31QhE7hk/edit', 'Deeplearning'),
('cba33ebd-a213-4505-bfda-df001838c43a', '1. Game Elements with Canvas_deeplearning', 'https://docs.google.com/document/d/1oc299CNRX1cDBbfGYAxYmvvbKtPsoEuLeBG-eTn07lY/edit', 'Deeplearning'),
('d1295092-0c1c-4679-b174-0a3ec7d40e93', '3. Phaser Game_看!有飛碟', 'https://docs.google.com/document/d/1h42extgvhvN6iRpC0YB5ikS5vo_RrPUm1q4ejR-Mgww/edit?usp=sharing', 'Surfacelearning');

-- --------------------------------------------------------

--
-- 資料表結構 `sample_project_bean_samplecodes`
--

CREATE TABLE `sample_project_bean_samplecodes` (
  `sample_project_bean_projectid_ex` varchar(255) NOT NULL,
  `samplecodes_codeid` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 傾印資料表的資料 `sample_project_bean_samplecodes`
--

INSERT INTO `sample_project_bean_samplecodes` (`sample_project_bean_projectid_ex`, `samplecodes_codeid`) VALUES
('0ab02719-8721-416e-ba2a-027713634404', '38745b50-a32b-4edb-9da5-9935985f4ad4'),
('0ab02719-8721-416e-ba2a-027713634404', '64f09b71-6a2f-4afe-9df9-97cd0e5ffc06'),
('38889cb6-9e26-47c6-8434-aa8fd442db19', '058cb757-1fa7-431e-9329-aff3232b195d'),
('38889cb6-9e26-47c6-8434-aa8fd442db19', '6f951eca-1dbb-4e0e-93a5-8584f827aa1f'),
('38889cb6-9e26-47c6-8434-aa8fd442db19', 'e2d0aa3b-42db-43d0-94b6-33558992f60b'),
('79903bef-6ec6-4a0a-a426-3a8bf742912d', '01783354-0230-465e-8535-8ea86bcae88c'),
('79903bef-6ec6-4a0a-a426-3a8bf742912d', 'b0183b0d-4b77-4817-a42c-d29db5079758'),
('7a138710-a593-4b7c-b17e-6da5a1bce954', 'ef84f163-9fc4-48d6-b375-f1b86250259a'),
('b200b707-6828-4da9-891d-0128c3fb499d', 'bf21e8c3-302d-40eb-86ae-59e502054455'),
('b5b6adb9-3d4b-40ee-8e34-a59d651a455f', '29a5bcc9-d05f-4b65-b5a7-5c2244f5ca88'),
('b5b6adb9-3d4b-40ee-8e34-a59d651a455f', '2d7bf9c4-4a76-45be-92c4-a54b9d224b51'),
('b5b6adb9-3d4b-40ee-8e34-a59d651a455f', 'f421a1ea-6627-431f-ba11-c45e05664812'),
('b929da83-c3fc-451a-99da-999a66debb60', '2c391b43-db34-465e-a394-061e13f4381d'),
('b929da83-c3fc-451a-99da-999a66debb60', '4ad1d5d2-6be1-4fce-b097-51b990277589'),
('cba33ebd-a213-4505-bfda-df001838c43a', '576dd1a5-c7ac-4b46-95f9-998b6fcdb755'),
('d1295092-0c1c-4679-b174-0a3ec7d40e93', '2c0919ec-bd11-4284-9eac-1a5fc2d2173b'),
('d1295092-0c1c-4679-b174-0a3ec7d40e93', '8c836d0b-ea65-4536-bd6e-79b9341c174c'),
('d1295092-0c1c-4679-b174-0a3ec7d40e93', 'fc1469f7-f716-43e4-9057-304fe5401011');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `note_info_bean`
--
ALTER TABLE `note_info_bean`
  ADD PRIMARY KEY (`noteid`);

--
-- 資料表索引 `project_bean`
--
ALTER TABLE `project_bean`
  ADD PRIMARY KEY (`projectid`);

--
-- 資料表索引 `project_bean_codeinfoes`
--
ALTER TABLE `project_bean_codeinfoes`
  ADD UNIQUE KEY `UK_o9k2aycdmhpr8440n1a1xtrqu` (`codeinfoes_codeid`),
  ADD KEY `FK15wvcoe6lopixpvl6miiruy5a` (`project_bean_projectid`);

--
-- 資料表索引 `sample_code_bean`
--
ALTER TABLE `sample_code_bean`
  ADD PRIMARY KEY (`codeid`),
  ADD KEY `FKg8lsgiipc3yysl0b5gmke74jj` (`note_info_bean_noteid`);

--
-- 資料表索引 `sample_project_bean`
--
ALTER TABLE `sample_project_bean`
  ADD PRIMARY KEY (`projectid_ex`);

--
-- 資料表索引 `sample_project_bean_samplecodes`
--
ALTER TABLE `sample_project_bean_samplecodes`
  ADD UNIQUE KEY `UK_ltje96n7rbq3etsbqsgmpagtn` (`samplecodes_codeid`),
  ADD KEY `FK6tr91mrg3x7lfuhewdtcefwhb` (`sample_project_bean_projectid_ex`);

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `project_bean_codeinfoes`
--
ALTER TABLE `project_bean_codeinfoes`
  ADD CONSTRAINT `FK15wvcoe6lopixpvl6miiruy5a` FOREIGN KEY (`project_bean_projectid`) REFERENCES `project_bean` (`projectid`),
  ADD CONSTRAINT `FKt90q06ppcgvdirlgvwa0bk046` FOREIGN KEY (`codeinfoes_codeid`) REFERENCES `sample_code_bean` (`codeid`);

--
-- 資料表的限制式 `sample_code_bean`
--
ALTER TABLE `sample_code_bean`
  ADD CONSTRAINT `FKg8lsgiipc3yysl0b5gmke74jj` FOREIGN KEY (`note_info_bean_noteid`) REFERENCES `note_info_bean` (`noteid`);

--
-- 資料表的限制式 `sample_project_bean_samplecodes`
--
ALTER TABLE `sample_project_bean_samplecodes`
  ADD CONSTRAINT `FK6r293q7j6pkot8mwg449a3okp` FOREIGN KEY (`samplecodes_codeid`) REFERENCES `sample_code_bean` (`codeid`),
  ADD CONSTRAINT `FK6tr91mrg3x7lfuhewdtcefwhb` FOREIGN KEY (`sample_project_bean_projectid_ex`) REFERENCES `sample_project_bean` (`projectid_ex`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
