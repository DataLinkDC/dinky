<#ftl output_format="HTML">
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Dinky Alert:${title}</title>
</head>
<body
        style="
      background-color: #f4f4f4;
      margin: 0;
      padding: 0;
      justify-content: center;
      align-items: center;
    "
>
<table
        align="center"
        cellpadding="0"
        cellspacing="0"
        width="100%"
        style="
        max-width: 600px;
        background-color: #fff;
        border-radius: 5px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      "
>
    <tr>
        <td align="center" bgcolor="#007bff" style="padding: 10px">
            <h1 style="color: #fff; margin: 0">Dinky Alert</h1>
        </td>
    </tr>
    <tr>
        <td style="padding: 20px">
            <p>Dear Dinky User:</p>
            <p>Your task there is an abnormality:${title}, Please troubleshoot</p>
            ${content?no_esc}
        </td>
    </tr>
    <tr>
        <td bgcolor="#f4f4f4" align="center" style="padding: 10px">
            <p>Thank you for your support of our open source project!</p>
            <p>
                If you have any questions or suggestions, please ask questions or
                submit requests on the GitHub repository.
                <a
                        href="https://github.com/your-username/your-project"
                        target="_blank"
                >GitHub</a
                >
            </p>
        </td>
    </tr>
</table>
</body>
</html>
