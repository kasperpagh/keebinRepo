/**
 * Created by kaspe on 03-05-2017.
 */
let expect = require("chai").expect;
let mocha = require("mocha");
let bcrypt = require("bcryptjs");
let request = require("request");

describe("Test Login API (inkl. createUser)", function ()
{
    let userToSave =
        {
            "firstName": "tester",
            "lastName": "tester",
            "email": "Apitest@test.test",
            "roleId": 1,
            "birthday": "09/09/2010",
            "sex": "male",
            "password": "123"
        };

    let headers;
    
    describe("Test af login, indeholde både hashing, creation af tokens og save deraf", function ()
    {
        it("Laver en ny test user", function (done)
        {
            var options = {
                method: 'post',
                body: {
                    "firstName": "tester",
                    "lastName": "tester",
                    "email": "Apitest@test.test",
                    "roleId": "1",
                    "birthday": "09/09/2010",
                    "sex": "male",
                    "password": "123"
                },
                json: true,
                url: "http://localhost:8080/login/user/new"
            };

            request(options, (error, response, body) =>
            {
                headers = response.headers;
                console.log("her er headers: " + Object.keys(headers) + headers.refreshtoken);
                expect(response.statusCode).to.equal(200);
                done();
            });
        });
    });

    describe("Finder den nye user og verificerer hans korrekthed (test af GetUser API)", function ()
    {
        it("Finder og sammenligner userToSave og resultatet", function (done)
        {
            let options =
                {
                    method: 'get',
                    headers: {
                        "refreshtoken": headers.refreshtoken
                    },
                    url: "http://localhost:8080/api/users/user/Apitest@test.test"
                };
            request(options, function (error, response, body)
            {
                let parsedBody = JSON.parse(response.body);
                expect(response.statusCode).to.equal(200);
                expect(parsedBody.email).to.equal("Apitest@test.test");
                //should not equal the given PW, since the one returned should be hashed!
                expect(parsedBody.password).to.not.equal("123");
                headers.refreshToken = response.headers.refreshtoken;
                done();
            });
        });
    });

    describe("Finder den nye user og verificerer hans korrekthed (test af GetUser API)", function ()
    {
        it("Finder og sammenligner userToSave og resultatet", function (done)
        {
            let options =
                {
                    method: 'delete',
                    headers: {
                        "refreshtoken": headers.refreshtoken
                    },
                    url: "http://localhost:8080/api/users/user/Apitest@test.test"
                };
            request(options, function (error, response, body)
            {
                expect(response.statusCode +"").to.equal("200");
                // done();
                headers.refreshToken = response.headers.refreshtoken;
                let options2 =
                    {
                        method: 'get',
                        headers: {
                            "refreshtoken": headers.refreshtoken
                        },
                        url: "http://localhost:8080/api/users/user/Apitest@test.test"
                    };
                request(options2, function (error, response, body)
                {
                    //Hvis brugeren er slette virker denne token ikke mere, hvorfor dette kald skal give not allowed (401)!
                    expect(response.statusCode).to.equal(401);
                    done();
                });
            });
        });
    });

});