// ==UserScript==
// @name        moodleZipDL
// @namespace   sakailab
// @include     https://edu2.cs.inf.shizuoka.ac.jp/in/moodle22/mod/assignment/submissions*
// @version     1
// @grant       none
// @require http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js
// @require https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2014-11-29/FileSaver.min.js
// ==/UserScript==

// firefoxに対応
(function(){
    var temp = document.createElement("div");
    if (temp.innerText == undefined) {
        Object.defineProperty(HTMLElement.prototype, "innerText", {
            get: function()  { return this.textContent },
            set: function(v) { this.textContent = v; }
        });
    }
})();

// classから必要な要素のみ抽出
var sName = document.getElementsByClassName('cell c1 fullname');
var sNum = document.getElementsByClassName('cell c2 email');
var zp = document.getElementsByClassName('cell c5 timemodified');

// zipファイルのpathを取得するパターン
var zipURLPattarn = /https\:\/\/edu2\.cs\.inf\.shizuoka\.ac\.jp\/in\/moodle22\/pluginfile\.php\/\d{4}\/mod\_assignment\/submission\/\d{4}\/.+?\.zip/;

// DOMから学生の名前を抽出
var studentNames = [];
for (var i = 0; i < sName.length; i++) {
    studentNames[i] = sName[i].innerText;
}
console.log(studentNames.length + "件の学生の名前を抽出");

// メールアドレスから学籍番号を抽出
var studentNumber = [];
for(var i = 0; i < sNum.length; i++){
    var tempIa = sNum[i].innerText.match(/ia/);
    if(tempIa!=null && tempIa.toString() === "ia"){
        var temp = sNum[i].innerText.match(/\d{5}/).toString();
        studentNumber[i] = "70" + temp[1] + temp[0] + "1" + temp[2] + temp[3] + temp[4];
    }else{
        studentNumber[i] = "XXXXXXXX";
        console.log("iaで始まらないメールアドレスがあります ： " + sNum[i].innerText);
    }
}
console.log(studentNumber.length + "件の学籍番号を抽出");

var missSubmit = 0;
// DOMからzipファイルのpathを抽出
var zipPaths = [];
for (var i = 0; i < zp.length; i++) {
    zipPaths[i] = zp[i].innerHTML.match(zipURLPattarn);
    if(zipPaths[i] ==null&&zp[i].innerHTML.match(/:/)){
        missSubmit++;
    }
}
console.log(zipPaths.length + "件のZIPFILEを抽出");
/*
//DOMから"lectureXX"の数字を取得
var lec = null;
for(var i= 0;lec==null&&i<zp.length;i++){
    lec = zp[i].innerText.match(/lecture\d{2}/);
}
// もし取得できなければXXをそのままセット
if(lec==null){
	lec  =  "lectureXX";
}
*/

// 入力欄を埋め込み
var dLButton = document.createElement("input");
dLButton.setAttribute("type", "text");
dLButton.setAttribute("id", "zipDLName");
dLButton.setAttribute("value", "lectureXX");
document.getElementsByClassName('mod-assignment-download-link')[0].appendChild(dLButton);

// ダウンロードボタンを埋め込み
var dLButton = document.createElement("button");
dLButton.setAttribute("type", "button");
dLButton.setAttribute("id", "zipDLButton");
dLButton.innerHTML="Download ALL ZIP in this page";

document.getElementsByClassName('mod-assignment-download-link')[0].appendChild(dLButton);


// ダウンロードしたファイルの数
var downZipCount = 0;
var notSubmitter=[];
var getRequest = true;
var errorFlag = false;

var downloadFile = function () {
    var zipName = document.getElementById('zipDLName').value;
    if(downZipCount < zipPaths.length){// ダウンロードした数を数える
        console.log((downZipCount+1)+ "/" + zipPaths.length)
        if(zipPaths[downZipCount] != null){// 未提出者の場合ははじく
	
            var url = zipPaths[downZipCount];
            var xhr = new XMLHttpRequest(),
                deferred = new $.Deferred();
            // サーバからリクエストが帰ってきた時のリスナ
            xhr.addEventListener('load', function () {
	      // 名前をつけてzipファイルを保存
                saveAs(new Blob([xhr.response]),studentNumber[downZipCount]+"-"+zipName+".zip"); 
                console.log("　　"+(downZipCount+1)+"Request received");
                if(getRequest){
                    console.log("Saveされていない課題がある可能性があります"+ studentNumber[downZipCount]);
                    errorFlag = ture;
                }
                getRequest = true;
                // インクリメントし，次のリクエストを送る
                downZipCount++;
                downloadFile();
                deferred.resolve(xhr);

            });
	// リクエストを作成し送信
            xhr.open('GET', url, true);
            xhr.responseType = 'arraybuffer'; // <- ここでarraybufferを設定
            xhr.send();
            console.log("　　"+(downZipCount+1)+"Request send");
            getRequest =false;
            return deferred.promise();
        }else{// 未提出者の場合はスキップ
            notSubmitter.push(downZipCount);
            console.log(sNum[downZipCount].innerText+" not Submitter")
            downZipCount++;
            downloadFile();
        }
    }else{
        console.log("未提出者"+notSubmitter.length+"名");
        for(var i=0;i<notSubmitter.length;i++){
            console.log(studentNumber[notSubmitter[i]]);
        }
        if(errorFlag){
            alert("正常にDLできませんでした．DL出来ていないファイルがある可能性があります．Consolelogを参照してください．");
        }else if(missSubmit>0){
            alert("zipファイル"+(downZipCount-notSubmitter.length)+"件のダウンロードおわり."+missSubmit+"件はzipファイル提出なし．");
        }else{
            alert("zipファイル"+(downZipCount-notSubmitter.length)+"件のダウンロードおわり");
        }
    }
}

document.getElementById("zipDLButton").onclick=function(){
    downloadFile();// ダウンロード開始
}

