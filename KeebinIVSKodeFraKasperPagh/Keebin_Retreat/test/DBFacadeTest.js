/**
 * Created by kaspe on 02-05-2017.
 */
let expect = require("chai").expect;
let mocha = require("mocha");
let facade = require("../JS/Facade/DataBaseFacade");
let bcrypt = require("bcryptjs");


describe("Tests the most commonly used user related DB facade functions (essentially just CRUD)", function ()
{
    var salt = bcrypt.genSaltSync(12);
    let testUser =
        {
            firstName: "test",
            lastName: "test",
            email: "testing@testing.test",
            role: 1,
            birthday: "09/09/2010",
            sex: "male",
            password: bcrypt.hashSync("123", salt)
        };
    describe("Tests the createUser function", function ()
    {
        it("creates the user, and returns true", function (done)
        {
            facade.createUser(testUser.firstName, testUser.lastName, testUser.email, testUser.role, testUser.birthday, testUser.sex, testUser.password, function (data)
            {
                expect(data).to.equal(true);
                done();
            });
        });
    });

    describe("Tests the getUser function", function ()
    {
        it("Finds the user with the email: testing@testing.test, and returns it", function (done)
        {
            facade.getUser("testing@testing.test", function (data)
            {
                expect(data.dataValues.firstName).to.equal(testUser.firstName);
                expect(data.dataValues.lastName).to.equal(testUser.lastName);
                expect(data.dataValues.email).to.equal(testUser.email);
                expect(data.dataValues.roleId).to.equal(testUser.role);
                expect(data.dataValues.sex).to.equal(testUser.sex);
                done();
            });
        });
    });

    describe("Tests the putUser (edit) function", function ()
    {
        it("Finds the user with the email: testing@testing.test, and updates it with new data", function (done)
        {
            facade.putUser("123", "testing@testing.test", "testerÆndret", "testerÆndret", "nytester@email.test", 1, "01/01/2010", "female", "1234", function (data)
            {
                let editedUser = data.dataValues;
                expect(editedUser.firstName).to.equal("testerÆndret");
                expect(editedUser.lastName).to.equal("testerÆndret");
                expect(editedUser.email).to.equal("nytester@email.test");
                expect(editedUser.roleId).to.equal(1);
                expect(editedUser.sex).to.equal("female");
                done();
            });
        });
    });

    describe("Tests the deleteUser function", function ()
    {
        it("Finds and deletes the user", function (done)
        {
            facade.deleteUser("nytester@email.test", function (data)
            {
                expect(data).to.equal(true);
                done();
            });

        });
    });
});


describe("Test af CoffeeBrand og CoffeeShop", function ()
{
    let testBrand;
    let testShop;
    let editedShop;

    describe("Test af createCoffeeBrand", function ()
    {
        it("Laver et brand ved navn test", function (done)
        {
            facade.createCoffeeBrand("Test", 27, function (data)
            {
                expect(data).to.equal(true);
                done();
            });

        });
    });

    describe("Test af getCoffeeBrand", function ()
    {
        it("Finder den nye brand, m. getAll (den eneste mulighed for at finde på navn)", function (done)
        {
            facade.getAllCoffeeBrand(function (data)
            {
                for (let i = 0; i < data.length; i++)
                {
                    if (data[i].brandName === "Test")
                    {
                        testBrand = data[i];
                        console.log("HER ER TESTBRAND " + Object.keys(testBrand) + testBrand.id + testBrand.brandName);
                        expect(data[i].brandName).to.equal("Test");
                        expect(data[i].numberOfCoffeeNeeded).to.equal(27);
                        break;
                    }
                }
                done();
            });
        });
    });


    describe("Test af createShop", function ()
    {
        it("Laver en ny shop, med det nye brand (ovenstående)", function (done)
        {
            facade.createCoffeeShop("test@shop.test", testBrand.id, "testervej 5", "12345678", "1234", 27, 48, function (data)
            {
                expect(data).to.equal(true);
                done();
            });
        });
    });

    describe("Test af editShop", function ()
    {
        it("Ændre den nye test shop", function (done)
        {

            facade.putCoffeeShop("test@shop.test", "nyshopemail@shop.test", testBrand.id, "Ny testervej 5", "87654321", "4321", 27, 48, function (data)
            {
                editedShop = data.dataValues;
                expect(editedShop.email).to.equal("nyshopemail@shop.test");
                expect(editedShop.address).to.equal("Ny testervej 5");
                expect(editedShop.phone).to.equal("87654321");
                done();
            });
        });
    });


    describe("Test af getShop", function ()
    {
        it("Finder den ændrede shop, igen", function (done)
        {
            facade.getCoffeeShop("nyshopemail@shop.test", data =>
            {
                expect(data.email).to.equal(editedShop.email);
                expect(data.address).to.equal(editedShop.address);
                expect(data.phone).to.equal(editedShop.phone);
                done();
            });
        });
    });


    describe("Test af deleteShop", function ()
    {
        it("Fjerner test shoppen", function (done)
        {
            facade.deleteCoffeeShop("nyshopemail@shop.test", data =>
            {
                expect(data).to.equal(true);
                done();
            });
        });
    });

    describe("Test af deleteBrand", function ()
    {
        it("Fjerne test brand", function (done)
        {
            facade.deleteCoffeeBrand(testBrand.id, data =>
            {
                expect(data).to.equal(true);
                done();
            });
        });
    });


});


